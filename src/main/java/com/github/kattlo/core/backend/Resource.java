package com.github.kattlo.core.backend;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents the actual resource state
 *
 * @author fabiojose
 */
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

}
