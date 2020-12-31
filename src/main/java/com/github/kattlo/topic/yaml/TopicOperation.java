package com.github.kattlo.topic.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Original;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.topic.TopicCommandException;
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

    private static final String DEFAULT_CONTENT_TYPE = "text/yaml";

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

    public Migration toMigration() {
        var result = new Migration();

        result.setVersion(getVersion());
        result.setOperation(OperationType.valueOf(getOperation().toUpperCase()));
        result.setNotes(getNotes());
        result.setResourceType(ResourceType.TOPIC);
        result.setResourceName(getTopic());
        result.setTimestamp(LocalDateTime.now());

        var attributes = Map.of(
            "partitions", String.valueOf(getPartitions()),
            "replicationFactor", String.valueOf(getReplicationFactor()),
            "config", Map.copyOf(getConfig())
        );
        result.setAttributes(attributes);

        var original = new Original();
        original.setPath(getFile().toString());
        original.setContentType(DEFAULT_CONTENT_TYPE);

        try {
            var contentBytes = Files.readAllBytes(getFile());
            var contentBase64 = Base64.getEncoder().encodeToString(contentBytes);
            original.setContent(contentBase64);

            result.setOriginal(original);

        }catch(IOException e) {
            throw new TopicCommandException(e.getMessage(), e);
        }

        return result;
    }
}
