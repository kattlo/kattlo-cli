package com.github.kattlo.topic.migration;

import com.github.kattlo.core.backend.OperationType;

import java.util.Objects;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

/**
 * @author fabiojose
 */
public interface Strategy {

    void execute(AdminClient admin);

    static Strategy of(TopicOperation operation) {
        Objects.requireNonNull(operation);

        if(OperationType.CREATE.name().toLowerCase()
                .equals(operation.getOperation())){

            return new CreateStrategy(operation);

        } else if(OperationType.PATCH.name().toLowerCase()
                .equals(operation.getOperation())){

            return new PatchStrategy(operation);
        }

        return new RemoveStrategy(operation);
    }
}
