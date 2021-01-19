package com.github.kattlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import com.github.kattlo.core.backend.ResourceType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.yaml.snakeyaml.Yaml;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GenMigrationCommandTest {

    @Mock
    CommandSpec spec;

    @InjectMocks
    GenMigrationCommand command;

    @Test
    public void should_throw_when_directory_does_not_exist() {

        // setup
        var directory = new File("./build/tmp/not_found");

        command.setDirectory(directory);
        command.setResource(ResourceType.TOPIC);

        when(spec.commandLine())
            .thenReturn(new CommandLine(command));

        assertThrows(CommandLine.ParameterException.class, () ->
            command.run());
    }

    @Test
    public void should_write_the_topic_migration_file() throws IOException {

        // setup
        var directory = new File("./build/tmp/gen-topic-migration-0");
        directory.mkdirs();

        var expected = new File(directory, "v0001_rename-me.yaml");

        command.setDirectory(directory);
        command.setResource(ResourceType.TOPIC);

        // act
        command.run();

        // assert
        assertTrue(expected.exists());

        var yaml = new Yaml();
        var actual = yaml.loadAs(new FileInputStream(expected), Map.class);
        assertEquals("create", actual.get("operation"));
        assertEquals("topic-name", actual.get("topic"));
        assertEquals(1, actual.get("partitions"));
        assertEquals(1, actual.get("replicationFactor"));
        assertNotNull(actual.get("config"));

    }

    @Test
    public void should_gen_topic_migration_with_v0001_if_no_migrations() {

        // setup
        var directory = new File("./build/tmp/gen-topic-migration-1");
        directory.mkdirs();

        var expected = new File(directory, "v0001_rename-me.yaml");

        command.setDirectory(directory);
        command.setResource(ResourceType.TOPIC);

        // act
        command.run();

        // assert
        assertTrue(expected.exists());

    }

    @Test
    public void should_gen_topic_migration_with_right_version_if_migrations() throws IOException {

        // setup
        var directory = new File("./build/tmp/gen-topic-migration-2");
        directory.mkdirs();

        var v0001 = new File(directory, "v0001_create-topic.yaml");
        v0001.createNewFile();

        var expected = new File(directory, "v0002_rename-me.yaml");

        command.setDirectory(directory);
        command.setResource(ResourceType.TOPIC);

        // act
        command.run();

        // assert
        assertTrue(expected.exists());
    }
}
