package com.github.kattlo.core.backend.file.yaml.model;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * @author fabiojose
 */
@Data
@RegisterForReflection
public class Current {

    private String topic;
    private String status;
    private String version;
    private String timestamp;
    private Integer partitions;
    private Integer replicationFactor;
    private Map<String, Object> config;

    private List<History> history;

}
