package com.github.kattlo.core.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationToApply {

    @NonNull private String version;
    @NonNull private OperationType operation;
    @NonNull private ResourceType resourceType;
    @NonNull private String resourceName;

    /**
     * MIME type of content
     */
    @NonNull private String contentType;

    /**
     * Migration content
     */
    @NonNull private byte[] content;

    private String notes;
}
