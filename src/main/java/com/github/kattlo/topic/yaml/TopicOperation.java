package com.github.kattlo.topic.yaml;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.util.StringUtil;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author fabiojose
 */
@Getter
@EqualsAndHashCode
@ToString
public class TopicOperation {

    private String version;
    private String operation;
    private String notes;
    private String topic;
    private Integer partitions;
    private Integer replicationFactor;

    private Map<String, Object> config;

    private Path file;

    @Builder(toBuilder = true)
    TopicOperation(String version, String operation, String notes,
        String topic, Integer partitions, Integer replicationFactor,
        Map<String, Object> config, Path file){

        this.version = StringUtil.requireNonBlank(version);

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

        if(!Objects.isNull(partitions) && partitions < 1){
            throw new IllegalArgumentException(
                    "partitions must be greater than zero");
        }
        if(!Objects.isNull(replicationFactor) && replicationFactor < 1){
            throw new IllegalArgumentException(
                    "replication factor must be greater than zero");
        }

        this.partitions = partitions;
        this.replicationFactor = replicationFactor;
        this.config = Objects.requireNonNullElse(config, new HashMap<>());

        this.file = Objects.requireNonNull(file);
    }
}
