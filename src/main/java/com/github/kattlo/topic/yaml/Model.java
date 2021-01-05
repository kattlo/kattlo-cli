package com.github.kattlo.topic.yaml;

import java.util.Map;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * @author fabiojose
 */
@Data
@RegisterForReflection
public class Model {

    private String operation;
    private String notes;
    private String topic;
    private Integer partitions;
    private Integer replicationFactor;

    private Map<String, Object> config;

    public Map<String, Object> asMap() {
        return Map.of(
            "operation", getOperation(),
            "notes", getNotes(),
            "topic", getTopic(),
            "partitions", getPartitions(),
            "replicationFactor", getReplicationFactor(),
            "config",(Objects.nonNull(getConfig()) ? Map.copyOf(getConfig()) : Map.of())
        );
    }
}
