package com.github.kattlo.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.topic.migration.Strategy;
import com.github.kattlo.topic.yaml.TopicOperation;
import com.github.kattlo.topic.yaml.TopicOperationMapper;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
//@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class TopicCommandTest {

    private TopicCommand topic = new TopicCommand();

    @Mock
    EntryCommand parent;

    @Mock
    CommandSpec spec;

    @Mock
    Backend backend;

    @Mock
    Kafka kafka;

    @Mock
    AdminClient admin;

    @Mock
    Strategy strategy;

    @Spy
    TopicOperationMapper mapper =
        Mappers.getMapper(TopicOperationMapper.class);

    @InjectMocks
    @Spy
    TopicCommand command;

    @Captor
    ArgumentCaptor<TopicOperation> topicOperationCaptor;

    @Test
    public void should_exit_code_2_when_directory_not_exists() {

        String[] args = {
            "--config-file=./src/test/java/resources/.kattlo.yaml",
            "--kafka-cfg=./src/test/java/resources/kafka.properties",
            "topic",
            "--directory=./_not_exists"
        };

        int actual =
            new CommandLine(topic).execute(args);

        assertEquals(2, actual);
    }

    @Test
    public void should_execute_create_strategy() {

        // setup
        final String topic = "01_try_to_create_topic";
        final File directory = new File("./src/test/resources/topics/01_try_to_create_topic/");

        //when(parent.getConfiguration())
        //    .thenReturn(new Properties());

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.empty());

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        //when(command.strategyOf(to))
        //    .thenReturn(strategy);

        command.setDirectory(directory);

        // act
        command.run();

        verify(command).strategyOf(topicOperationCaptor.capture());
        var actual = topicOperationCaptor.getValue();

        // assert
        assertEquals(topic, actual.getTopic());
        assertEquals(1, actual.getPartitions());
        assertEquals(1, actual.getReplicationFactor());

    }


    @Test
    public void should_execute_create_and_patch_strategies() {

        // setup
        final String topic = "02_try_to_create_topic_patch_partitions";
        final File directory = new File("./src/test/resources/topics/02_try_to_create_topic_patch_partitions/");

        //when(parent.getConfiguration())
        //    .thenReturn(new Properties());

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.empty());

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        //when(command.strategyOf(to))
        //    .thenReturn(strategy);

        command.setDirectory(directory);

        // act
        command.run();

        verify(command, times(2)).strategyOf(topicOperationCaptor.capture());
        var actual = topicOperationCaptor.getAllValues();

        // assert
        assertEquals(2, actual.size());

        var create = actual.get(0);
        assertEquals(topic, create.getTopic());
        assertEquals(2, create.getPartitions());
        assertEquals(1, create.getReplicationFactor());

        var patch = actual.get(1);
        assertEquals(topic, patch.getTopic());
        assertEquals(3, patch.getPartitions());
        assertNull(patch.getReplicationFactor());

    }

    @Test
    public void should_create_topic() {

    }
}
