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
    @NonNull private String source;

    @NonNull private OperationType operation;
    private String notes;

    @NonNull private ResourceType resourceType;

    @NonNull private String resourceName;

}
