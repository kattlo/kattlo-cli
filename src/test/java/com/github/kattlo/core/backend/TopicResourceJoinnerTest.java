package com.github.kattlo.core.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class TopicResourceJoinnerTest {

    private final TopicResourceJoinner joinner = new TopicResourceJoinner();

    @Test
    public void should_return_the_new_resource_map_of_properties() {

    }

    @Test
    public void should_join_the_values_of_config_map() {

        // setup
        var leftConfig = Map.of(
            "compression.type", "lz4"
        );

        var leftAttributes = Map.of(
            "replicationFactor", 2,
            "config", leftConfig
        );

        var left = Map.of(
            "resourceName", "joinner-topic-name-0",
            "resourceType", "TOPIC",
            "version", "v0001",
            "operation", "CREATE",
            "notes", "some notes",
            "attributes", leftAttributes,
            "history", new ArrayList<>()
        );

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("retention.bytes", "1024");

        var right = new Migration();
        right.setAttributes(Map.of(
            "partitions", "7",
            "config", config
        ));
        right.setNotes("retention.bytes and partitions");
        right.setOperation(OperationType.PATCH);
        right.setOriginal(original);
        right.setResourceName("topic-name-0");
        right.setResourceType(ResourceType.TOPIC);
        right.setTimestamp(LocalDateTime.now());
        right.setVersion("v0002");

        // act
        var actual = joinner.join(left, right);

        // assert
        var actualAttributes = (Map<String, Object>)actual.get("attributes");
        assertNotNull(actualAttributes);

        var actualConfig = (Map<String, Object>)actualAttributes.get("config");
        assertNotNull(actualConfig);
        assertEquals(2, actualConfig.size());

        assertEquals("lz4", actualConfig.get("compression.type"));
        assertEquals("1024", actualConfig.get("retention.bytes"));

    }

    @Test
    public void should_join_the_values_of_attributes() {

        // setup
        var leftConfig = Map.of(
            "compression.type", "lz4"
        );

        var leftAttributes = Map.of(
            "replicationFactor", "2",
            "config", leftConfig
        );

        var left = Map.of(
            "resourceName", "joinner-topic-name-0",
            "resourceType", "TOPIC",
            "version", "v0001",
            "operation", "CREATE",
            "notes", "some notes",
            "attributes", leftAttributes,
            "history", new ArrayList<>()
        );

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("retention.bytes", "1024");

        var right = new Migration();
        right.setAttributes(Map.of(
            "partitions", "7",
            "config", config
        ));
        right.setNotes("retention.bytes and partitions");
        right.setOperation(OperationType.PATCH);
        right.setOriginal(original);
        right.setResourceName("topic-name-0");
        right.setResourceType(ResourceType.TOPIC);
        right.setTimestamp(LocalDateTime.now());
        right.setVersion("v0002");

        // act
        var actual = joinner.join(left, right);

        // assert
        var actualAttributes = (Map<String, Object>)actual.get("attributes");
        assertNotNull(actualAttributes);

        assertEquals("2", actualAttributes.get("replicationFactor"));
        assertEquals("7", actualAttributes.get("partitions"));
    }

    @Test
    public void should_result_empty_config_map_when_no_config() {

        // setup
        var leftConfig = Map.of();

        var leftAttributes = Map.of(
            "replicationFactor", 2,
            "config", leftConfig
        );

        var left = Map.of(
            "resourceName", "joinner-topic-name-0",
            "resourceType", "TOPIC",
            "version", "v0001",
            "operation", "CREATE",
            "notes", "some notes",
            "attributes", leftAttributes,
            "history", new ArrayList<>()
        );

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of();

        var right = new Migration();
        right.setAttributes(Map.of(
            "partitions", "7",
            "config", config
        ));
        right.setNotes("retention.bytes and partitions");
        right.setOperation(OperationType.PATCH);
        right.setOriginal(original);
        right.setResourceName("topic-name-0");
        right.setResourceType(ResourceType.TOPIC);
        right.setTimestamp(LocalDateTime.now());
        right.setVersion("v0002");

        // act
        var actual = joinner.join(left, right);

        // assert
        var actualAttributes = (Map<String, Object>)actual.get("attributes");
        assertNotNull(actualAttributes);

        assertTrue(((Map<String, Object>)actualAttributes.get("config")).isEmpty());
    }

    @Test
    public void should_contains_the_common_properties() {

        // setup
        var leftConfig = Map.of(
            "compression.type", "lz4"
        );

        var leftAttributes = Map.of(
            "replicationFactor", 2,
            "config", leftConfig
        );

        var left = Map.of(
            "resourceName", "joinner-topic-name-0",
            "resourceType", "TOPIC",
            "version", "v0001",
            "operation", "CREATE",
            "notes", "some notes",
            "attributes", leftAttributes,
            "history", new ArrayList<>()
        );

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("retention.bytes", "1024");

        var right = new Migration();
        right.setAttributes(Map.of(
            "partitions", "7",
            "config", config
        ));
        right.setNotes("retention.bytes and partitions");
        right.setOperation(OperationType.PATCH);
        right.setOriginal(original);
        right.setResourceName("topic-name-0");
        right.setResourceType(ResourceType.TOPIC);
        right.setTimestamp(LocalDateTime.now());
        right.setVersion("v0002");

        // act
        var actual = joinner.join(left, right);

        // assert
        assertEquals("retention.bytes and partitions", actual.get("notes"));
        assertEquals("PATCH", actual.get("operation"));
        assertEquals("topic-name-0", actual.get("resourceName"));
        assertEquals("TOPIC", actual.get("resourceType"));
        assertNotNull(actual.get("timestamp"));
        assertEquals("v0002", actual.get("version"));
    }

}
