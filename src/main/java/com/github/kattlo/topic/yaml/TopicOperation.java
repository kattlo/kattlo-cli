package com.github.kattlo.topic.yaml;

import static java.util.AbstractMap.SimpleEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Original;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.yaml.MigrationLoader;
import com.github.kattlo.topic.TopicCommandException;
import com.github.kattlo.util.MachineReadableSupport;
import com.github.kattlo.util.StringUtil;
import com.github.kattlo.util.VersionUtil;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
// TODO rename to TopicMigration
@Slf4j
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

    @Getter(value = AccessLevel.NONE)
    private Map<String, Object> config;

    @Getter(value = AccessLevel.NONE)
    private Map<String, MachineReadableSupport> configReadable;

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

        this.config = Objects.requireNonNullElse(config,
            new HashMap<String, Object>());

        this.configReadable = this.config.entrySet().stream()
            .peek(kv -> log.debug("To machine readable: {}={}", kv.getKey(), kv.getValue()))
            .map(kv -> new SimpleEntry<String, MachineReadableSupport>(
                kv.getKey(),
                MachineReadableSupport.of(kv.getValue())))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

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
        result.setKattlo(VersionUtil.appVersion());

        var attributes = new HashMap<String, Object>();

        // set the human readable, if present
        attributes.put("config",
            getConfig().entrySet().stream()
                .map(kv -> Map.entry(kv.getKey(),
                    kv.getValue().getHumanReadable()
                        .map(v -> (Object)v)
                        .orElseGet(() -> kv.getValue().getMachineReadable())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
        );

        if(Objects.nonNull(getPartitions())){
            attributes.put("partitions",
                String.valueOf(getPartitions()));
        }

        if(Objects.nonNull(getReplicationFactor())){
            attributes.put("replicationFactor",
                String.valueOf(getReplicationFactor()));
        }

        result.setAttributes(Map.copyOf(attributes));

        var original = new Original();
        original.setPath(getFile().toString());
        original.setContentType(MigrationLoader.DEFAULT_CONTENT_TYPE);

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

    public Map<String, MachineReadableSupport> getConfig() {
        return configReadable;
    }
}
