package com.github.kattlo.core.backend.file.yaml.model;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * @author fabiojose
 */
@Data
@RegisterForReflection
public class History {

    private String version;
    private String operation;
    private String notes;
    private String timestamp;
    private Integer partitions;
    private Integer replicationFactor;

    private Map<String, Object> config;

    private Original original;
}
