package com.github.kattlo.topic.migration;

import static org.apache.kafka.clients.admin.NewPartitions.increaseTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewPartitionReassignment;
import org.apache.kafka.clients.admin.AlterConfigOp.OpType;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@RequiredArgsConstructor(access = AccessLevel.MODULE)
@Slf4j
public class PatchStrategy implements Strategy {

    private static final String DEFAULT_KEYWORD = "$default";
    private static final int FIRST_POSITION = 0;
    private static final int ZERO = 0;

    @NonNull
    private final TopicOperation operation;

    void patchPartitions(AdminClient admin){

        var newPartitions = increaseTo(operation.getPartitions());
        var partitions = Map.of(operation.getTopic(), newPartitions);

        log.debug("Partitions to increase {}", partitions);

        var result = admin.createPartitions(partitions);

        try {
            result.all().get();

            log.debug("Partitions of {} increased to {}", operation.getTopic(),
                operation.getPartitions());

        }catch(InterruptedException | ExecutionException e){
            throw new TopicPatchException(e.getMessage(), e);
        }
    }

    private ArrayList<Node> decreaseReplicationFactor(List<Node> currentReplicas, int toDecrease) {
        log.debug("Replication factor to decrease {}", toDecrease);

        var newReplicas = new ArrayList<>(currentReplicas);
        for(int index = currentReplicas.size() - 1, decrease = ZERO;
                index >= ZERO && decrease < Math.abs(toDecrease);
                index--, decrease++){

            var removed = newReplicas.remove(index);
            log.debug("Removed replica {}", removed);
        }

        return newReplicas;
    }

    void patchReplicationFactor(AdminClient admin) {

        // Fetch brokers list
        var cluster = admin.describeCluster();

        try{
            // Check the number of nodes
            var nodes = cluster.nodes().get();

            // is new size of replication factor less or equal of
            if(operation.getReplicationFactor() <= nodes.size()){

                // describe topic
                var result =
                    admin.describeTopics(Collections
                        .singletonList(operation.getTopic()));

                try {
                    final var details = result.all().get();
                    final var description = details.get(operation.getTopic());

                    final var toIncrease =
                        operation.getReplicationFactor()
                        - description.partitions().size();

                    log.debug("Replication factor to increase: {}", toIncrease);

                    var currentReplicas =
                      description.partitions().stream()
                        .collect(Collectors.toMap(
                            TopicPartitionInfo::partition,
                            TopicPartitionInfo::replicas));

                    final var newReplicas =
                      description.partitions().stream()
                        .peek(info ->
                            log.debug("Info of topic {}: {}",
                                operation.getTopic(), info))
                        .filter(info -> toIncrease > ZERO)
                        .filter(info -> !info.replicas().containsAll(nodes))
                        .map(info -> {
                            var candidate = new ArrayList<>(nodes);
                            candidate.removeAll(info.replicas());

                            return candidate;
                        })
                        .peek(candidate ->
                            log.debug("Candidate replicas {}", candidate))
                        .map(candidate ->
                            candidate.subList(FIRST_POSITION, toIncrease))
                        .flatMap(List::stream)
                        .distinct()
                        .peek(newOnes -> log.debug("New replicas {}", newOnes))
                        .collect(Collectors.toList());

                    // For each topic-partition
                    var newAssignments =
                      currentReplicas.entrySet().stream()
                        .peek(kv -> log.debug("Current Replicas {}", kv.getValue()))
                        .map(kv -> {
                            var newValue = new ArrayList<>(kv.getValue());
                            newValue.addAll(newReplicas);

                            if(toIncrease < ZERO){
                                newValue = decreaseReplicationFactor(newValue, toIncrease);
                            }

                            return Map.entry(kv.getKey(), newValue);
                        })
                        .peek(kv -> log.debug("New Replicas {}", kv.getValue()))
                        .map(kv -> {
                            var assigment = new NewPartitionReassignment(
                                kv.getValue().stream()
                                    .map(Node::id)
                                    .collect(Collectors.toList()));

                            return Map.entry(
                                new TopicPartition(operation.getTopic(), kv.getKey()),
                                    Optional.of(assigment));
                        })
                        .peek(kv -> log.debug("New Assignments: {}",
                                kv.getValue().get().targetReplicas()))
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

                    admin.alterPartitionReassignments(newAssignments);
                    log.debug("New assignments applied: {}", newAssignments);

                }catch(InterruptedException | ExecutionException e){
                    throw new TopicPatchException(e.getMessage(), e);
                }

            } else {
                throw new TopicPatchException("replication factor is greater than nodes: " + nodes.size());
            }

        }catch(InterruptedException | ExecutionException e){
            throw new TopicPatchException(e.getMessage(), e);
        }
    }

    private AlterConfigOp opFor(final ConfigEntry entry){

        if(DEFAULT_KEYWORD.equals(entry.value())){
            return new AlterConfigOp(entry, OpType.DELETE);
        }

        return new AlterConfigOp(entry, OpType.SET);
    }

    void patchConfig(AdminClient admin){

        var resource = new ConfigResource(Type.TOPIC, operation.getTopic());

        final Collection<AlterConfigOp> configs =
        operation.getConfig().entrySet().stream()
            .map(e -> new ConfigEntry(e.getKey(), e.getValue().toString()))
            .map(this::opFor)
            .collect(Collectors.toCollection(ArrayList::new));

        log.debug("Configurations to change: {}", configs);

        var alter = Map.of(resource, configs);
        var result = admin.incrementalAlterConfigs(alter);

        try{
            result.all().get();

            log.debug("Configurations of {} changed to {}",
                operation.getTopic(), configs);
        }catch(InterruptedException | ExecutionException e){
            throw new TopicPatchException(e.getMessage(), e);
        }
    }

    @Override
    public void execute(AdminClient admin) {
        log.debug("AdminClient {}", admin);
        log.debug("TopicOperation to perform {}", operation);

        if(!Objects.isNull(operation.getPartitions())){
            patchPartitions(admin);
        }

        if(!Objects.isNull(operation.getReplicationFactor())){
            patchReplicationFactor(admin);
        }

        if(!operation.getConfig().isEmpty()){
            patchConfig(admin);
        }

    }

}
