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

    public String key() {
        return keyFor(getResourceType(), getResourceName());
    }

    public static String keyFor(ResourceType type, String name) {
        return type + "_" + name;
    }

    private static String asString(Object o) {
        if(null== o){
            return null;
        }

        return o.toString();
    }

    @SuppressWarnings("unchecked")
    public static Migration2 from(Map<String, Object> map) {

        var result = new Migration2();

        result.setVersion(asString(map.get("version")));
        result.setOperation(OperationType.valueOf((String)map.get("operation")));
        result.setNotes(asString(map.get("notes")));
        result.setResourceName(asString(map.get("resourceName")));
        result.setResourceType(ResourceType.valueOf((String)map.get("resourceType")));
        result.setTimestamp(LocalDateTime.parse((String)map.get("timestamp"),
            DateTimeFormatter.ISO_DATE_TIME));
        result.setAttributes(Map.copyOf((Map<String, Object>)map.get("attributes")));
        result.setOriginal(Original.from((Map<String, Object>)map.get("original")));

        return result;
    }
}
