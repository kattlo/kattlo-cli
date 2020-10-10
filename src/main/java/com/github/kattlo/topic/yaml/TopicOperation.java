package com.github.kattlo.topic.yaml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.util.StringUtil;

import lombok.Builder;

/**
 * @author fabiojose
 */
public class TopicOperation {

    private String operation;
    private String notes;
    private String topic;
    private int partitions;
    private int replicationFactor;

    private Map<String, Object> config;

    @Builder(toBuilder = true)
    TopicOperation(String operation, String notes, String topic, int partitions,
        int replicationFactor, Map<String, Object> config){

        this.operation = StringUtil.requireNonBlank(operation);
        if(!Arrays.asList(OperationType.values()).stream()
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(Collectors.toList())
            .contains(this.operation)){

            throw new IllegalArgumentException(operation);
        }

        this.notes = notes;
        this.topic = StringUtil.requireNonBlank(topic);

        if(partitions < 1){
            throw new IllegalArgumentException(
                    "partitions must be greater than zero");
        }
        if(replicationFactor < 1){
            throw new IllegalArgumentException(
                    "replication factor must be greater than zero");
        }

        this.partitions = partitions;
        this.replicationFactor = replicationFactor;
        this.config = Objects.requireNonNullElse(config, new HashMap<>());
    }
}
