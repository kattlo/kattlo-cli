package com.github.kattlo.core.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.github.kattlo.core.backend.file.yaml.model.topic.Original;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents a migration version performed over a resource
 *
 * @author fabiojose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Migration2 {

    @NonNull private String version;

    @NonNull private OperationType operation;
    private String notes;
    @NonNull private ResourceType resourceType;
    @NonNull private String resourceName;
    @NonNull private LocalDateTime timestamp;

    /**
     * Attributes of migration
     */
    @NonNull private Map<String, Object> attributes;

    /**
     * Original content used to create the migration
     */
    @NonNull private Original original;

    public Map<String, Object> asMigrationMap() {
        return Map.of(
            "version"     , version,
            "operation"   , operation.name(),
            "notes"       , notes,
            "resourceName", resourceName,
            "resourceType", resourceType.name(),
            "timestamp"   , timestamp.format(DateTimeFormatter.ISO_DATE_TIME),
            "attributes"  , attributes,
            "original"    , original.asMap()
        );
    }

}
