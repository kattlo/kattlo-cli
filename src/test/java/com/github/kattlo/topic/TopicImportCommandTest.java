package com.github.kattlo.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.kafka.Kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TopicImportCommandTest {

    @Mock
    private Backend backend;

    @Mock
    private EntryCommand parentParent;

    @Mock
    private TopicCommand parent;

    @Mock
    private Kafka kafka;

    @Mock
    private AdminClient admin;

    @Mock
    private CommandSpec spec;

    @InjectMocks
    private TopicImportCommand command;

    @Captor
    private ArgumentCaptor<Migration> migrationCaptor;

    private void mockitoWhen() {

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        when(parent.getParent())
            .thenReturn(parentParent);

        when(parentParent.getKafkaConfiguration())
            .thenReturn(new Properties());

    }

    @Test
    public void should_fail_when_can_not_describe() {

        // setup
        final String topic = "topic-name-1";
        command.setTopicName(topic);

        final File directory = new File("./build/tmp/topic-name-1");
        directory.mkdirs();
        when(parent.getDirectory())
            .thenReturn(directory);

        mockitoWhen();

        var configs = List.of(
            new ConfigEntry("compression.type", "snappy")
        );
        var config = new Config(configs);

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenThrow(new InterruptedException("failure"));

            mocked.when(() -> TopicUtils.configsOf(anyString(), any()))
                .thenReturn(Optional.of(config));

            when(spec.commandLine())
                .thenReturn(new CommandLine(command));

            // act
            assertThrows(CommandLine.ExecutionException.class, () ->
                command.run());

        }
    }

    @Test
    public void should_fail_when_can_not_get_configs() {

        // setup
        final String topic = "topic-name-1";
        command.setTopicName(topic);

        final File directory = new File("./build/tmp/topic-name-1");
        directory.mkdirs();
        when(parent.getDirectory())
            .thenReturn(directory);

        mockitoWhen();

        var nodes = List.of(
            new Node(1, "localhost", 19092),
            new Node(2, "localhost", 29092),
            new Node(3, "localhost", 39092)
        );
        var leader = nodes.iterator().next();
        var replicas = nodes.subList(1, 2);
        var isr = replicas;

        var partitions = List.of(
            new TopicPartitionInfo(0, leader, replicas, isr),
            new TopicPartitionInfo(1, leader, replicas, isr)
        );
        var description = new TopicDescription(topic, false, partitions);

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.of(description));

            mocked.when(() -> TopicUtils.configsOf(anyString(), any()))
                .thenThrow(new InterruptedException("failure"));

            when(spec.commandLine())
                .thenReturn(new CommandLine(command));

            // act
            assertThrows(CommandLine.ExecutionException.class, () ->
                command.run());

        }
    }

    @Test
    public void should_fail_when_can_not_write_yaml_to_directory() {

        // setup
        final String topic = "topic-name-3";
        command.setTopicName(topic);

        final File directory = new File("./build/tmp/topic-name-3");
        when(parent.getDirectory())
            .thenReturn(directory);

        var nodes = List.of(
            new Node(1, "localhost", 19092),
            new Node(2, "localhost", 29092),
            new Node(3, "localhost", 39092)
        );
        var leader = nodes.iterator().next();
        var replicas = nodes.subList(1, 2);
        var isr = replicas;

        var partitions = List.of(
            new TopicPartitionInfo(0, leader, replicas, isr),
            new TopicPartitionInfo(1, leader, replicas, isr)
        );
        var description = new TopicDescription(topic, false, partitions);

        var configs = List.of(
            new ConfigEntry("compression.type", "snappy")
        );
        var config = new Config(configs);

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.of(description));

            mocked.when(() -> TopicUtils.configsOf(anyString(), any()))
                .thenReturn(Optional.of(config));

            when(spec.commandLine())
                .thenReturn(new CommandLine(command));

            // act
            assertThrows(CommandLine.ParameterException.class, () ->
                command.run());

        }
    }

    @Test
    public void should_write_yaml_to_directory() throws Exception {

        // setup
        final String topic = "topic-name-1";
        command.setTopicName(topic);

        final File directory = new File("./build/tmp/topic-name-1");
        directory.mkdirs();
        when(parent.getDirectory())
            .thenReturn(directory);

        mockitoWhen();

        var nodes = List.of(
            new Node(1, "localhost", 19092),
            new Node(2, "localhost", 29092),
            new Node(3, "localhost", 39092)
        );
        var leader = nodes.iterator().next();
        var replicas = nodes.subList(1, 2);
        var isr = replicas;

        var partitions = List.of(
            new TopicPartitionInfo(0, leader, replicas, isr),
            new TopicPartitionInfo(1, leader, replicas, isr)
        );
        var description = new TopicDescription(topic, false, partitions);

        var configs = List.of(
            new ConfigEntry("compression.type", "snappy")
        );
        var config = new Config(configs);

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.of(description));

            mocked.when(() -> TopicUtils.configsOf(anyString(), any()))
                .thenReturn(Optional.of(config));

            // act
            command.run();

            // assert
            var file = Path.of(directory.getAbsolutePath(),
                TopicImportCommand.INITIAL_FILE_NAME);

            assertTrue(file.toFile().exists());
        }
    }

    @Test
    public void should_commit_the_imported_topic() {

        // setup
        final String topic = "topic-name-2";
        command.setTopicName(topic);

        final File directory = new File("./build/tmp/topic-name-2");
        directory.mkdirs();
        when(parent.getDirectory())
            .thenReturn(directory);

        mockitoWhen();

        var nodes = List.of(
            new Node(1, "localhost", 19092),
            new Node(2, "localhost", 29092),
            new Node(3, "localhost", 39092)
        );
        var leader = nodes.iterator().next();
        var replicas = nodes.subList(0, 2);
        var isr = replicas;

        var partitions = List.of(
            new TopicPartitionInfo(0, leader, replicas, isr),
            new TopicPartitionInfo(1, leader, replicas, isr)
        );
        var description = new TopicDescription(topic, false, partitions);

        var configs = List.of(
            new ConfigEntry("compression.type", "lz4"),
            new ConfigEntry("retention.ms", "-1")
        );
        var config = new Config(configs);

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.of(description));

            mocked.when(() -> TopicUtils.configsOf(anyString(), any()))
                .thenReturn(Optional.of(config));

            // act
            command.run();

            // assert
            verify(backend).commit(migrationCaptor.capture());
            var actual = migrationCaptor.getValue();

            assertNotNull(actual);
            assertEquals("v0001", actual.getVersion());
            assertEquals(ResourceType.TOPIC, actual.getResourceType());
            assertEquals("topic-name-2", actual.getResourceName());
            assertEquals("2", actual.getAttributes().get("partitions"));
            assertEquals("2", actual.getAttributes().get("replicationFactor"));

            @SuppressWarnings("unchecked")
            var actualConfig = (Map<String, Object>)actual.getAttributes().get("config");
            assertEquals("lz4", actualConfig.get("compression.type"));
            assertEquals("-1", actualConfig.get("retention.ms"));
        }
    }

}
