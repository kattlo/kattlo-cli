package com.github.kattlo.topic.migration;

import static org.apache.kafka.clients.admin.NewPartitions.increaseTo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

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

        }
    }

    void patchReplicationFactor(AdminClient admin) {

        // TODO probally zook
    }

    void patchConfig(AdminClient admin){

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
