package com.github.kattlo.topic.migration;

import static org.apache.kafka.clients.admin.NewPartitions.increaseTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.AlterConfigOp.OpType;
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
            log.error(e.getMessage(), e);
            throw new TopicPatchException(e.getMessage(), e);
        }
    }

    void patchReplicationFactor(AdminClient admin) {

        // TODO probally zook
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
            log.error(e.getMessage(), e);
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
