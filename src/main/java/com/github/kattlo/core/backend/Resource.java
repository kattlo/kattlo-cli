package com.github.kattlo.core.backend;

import java.time.LocalDateTime;
import java.util.Map;

import com.github.kattlo.core.backend.kafka.ResourceCommit;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents the actual resource state
 *
 * @author fabiojose
 */
@RegisterForReflection
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    @NonNull private String version;

    @NonNull private ResourceStatus status;
    @NonNull private ResourceType resourceType;
    @NonNull private String resourceName;
    @NonNull private LocalDateTime timestamp;
    @NonNull private Map<String, Object> attributes;

    public Map<String, Object> asMap() {
        return Map.of(
            "version", version,
            "status", status,
            "resourceType", resourceType.name(),
            "resourceName", resourceName,
            "timestamp", timestamp,
            "attributes", Map.copyOf(attributes)
        );
    }

    public static Resource from(ResourceCommit commit) {
        if(null== commit){
            return null;
        }

        var resource = new Resource();
        resource.setVersion(commit.getVersion());
        resource.setStatus(ResourceStatus.AVAILABLE);
        if(OperationType.REMOVE.equals(commit.getOperation())){
            resource.setStatus(ResourceStatus.DELETED);
        }
        resource.setResourceType(commit.getResourceType());
        resource.setResourceName(commit.getResourceName());
        resource.setTimestamp(commit.getTimestamp());
        resource.setAttributes(Map.copyOf(commit.getAttributes()));


        return resource;
    }

}
