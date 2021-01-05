package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class WriterTest {

    private static Yaml YAML = new Yaml();

    @Test
    public void should_create_the_file_before_dump() throws Exception {

        // setup
        var model = new Model();
        model.setOperation("create");
        model.setNotes("notes");
        model.setTopic("topic-name-2");
        model.setPartitions(3);
        model.setReplicationFactor(1);

        var file = Path.of("./build/tmp/v0001_create-topic-name-2.yaml");

        // act
        Writer.write(model, file);
        var actual = (Map<String, Object>)
            YAML.load(new FileInputStream(file.toFile()));

        // assert
        assertNotNull(actual);
        assertEquals("create", actual.get("operation"));
        assertEquals("notes", actual.get("notes"));
        assertEquals("topic-name-2", actual.get("topic"));
        assertEquals(3, actual.get("partitions"));
        assertEquals(1, actual.get("replicationFactor"));
    }

    @Test
    public void should_write_the_attributes() throws Exception {

        // setup
        var model = new Model();
        model.setOperation("create");
        model.setNotes("notes");
        model.setTopic("topic-name");
        model.setPartitions(3);
        model.setReplicationFactor(1);

        var file = Path.of("./build/tmp/v0001_create-topic-name.yaml");
        Files.createFile(file);

        // act
        Writer.write(model, file);
        var actual = (Map<String, Object>)
            YAML.load(new FileInputStream(file.toFile()));

        // assert
        assertNotNull(actual);
        assertEquals("create", actual.get("operation"));
        assertEquals("notes", actual.get("notes"));
        assertEquals("topic-name", actual.get("topic"));
        assertEquals(3, actual.get("partitions"));
        assertEquals(1, actual.get("replicationFactor"));
    }

    @Test
    public void should_write_the_config() throws Exception {

        // setup
        var model = new Model();
        model.setOperation("create");
        model.setNotes("notes");
        model.setTopic("topic-name-1");
        model.setPartitions(3);
        model.setReplicationFactor(1);

        var config = Map.of(
            "compression.type", "snappy",
            "retention.ms", (Object)"-1"
        );

        model.setConfig(config);

        var file = Path.of("./build/tmp/v0001_create-topic-name-1.yaml");
        Files.createFile(file);

        // act
        Writer.write(model, file);
        var actualValue = (Map<String, Object>)
            YAML.load(new FileInputStream(file.toFile()));

        var actual = (Map<String, Object>)actualValue.get("config");

        // assert
        assertNotNull(actual);
        assertEquals("snappy", actual.get("compression.type"));
        assertEquals("-1", actual.get("retention.ms"));
    }
}
