package com.github.kattlo.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.MigrationToApply;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.topic.migration.Strategy;
import com.github.kattlo.topic.yaml.TopicOperation;
import com.github.kattlo.topic.yaml.TopicOperationMapper;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.common.KafkaFuture;
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
    CreateTopicsResult createTopicsResult;

    @Mock
    CreatePartitionsResult partitionsResult;

    @Mock
    KafkaFuture<Void> kafkaFuture;

    @Mock
    Strategy strategy;

    @Spy
    TopicOperationMapper mapper = Mappers.getMapper(TopicOperationMapper.class);

    @InjectMocks
    @Spy
    TopicCommand command;

    @Captor
    ArgumentCaptor<TopicOperation> topicOperationCaptor;

    private void mockitoWhen() throws Exception {

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        when(admin.createTopics(anyCollection()))
            .thenReturn(createTopicsResult);

        when(createTopicsResult.all())
            .thenReturn(kafkaFuture);

        when(kafkaFuture.get())
            .thenReturn((Void)null);

    }

    @Test
    public void should_exit_code_2_when_directory_not_exists() {

        String[] args = { "--config-file=./src/test/java/resources/.kattlo.yaml",
                "--kafka-cfg=./src/test/java/resources/kafka.properties", "topic", "--directory=./_not_exists" };

        int actual = new CommandLine(topic).execute(args);

        assertEquals(2, actual);
    }

    @Test
    public void should_execute_create_strategy() throws Exception {

        // setup
        final String topic = "01_try_to_create_topic";
        final File directory = new File("./src/test/resources/topics/01_try_to_create_topic/");

        // when(parent.getConfiguration())
        // .thenReturn(new Properties());

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.empty());

        mockitoWhen();

        // when(command.strategyOf(to))
        // .thenReturn(strategy);

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
    public void should_execute_create_and_patch_strategies() throws Exception {

        // setup
        final String topic = "02_try_to_create_topic_patch_partitions";
        final File directory = new File("./src/test/resources/topics/02_try_to_create_topic_patch_partitions/");

        // when(parent.getConfiguration())
        // .thenReturn(new Properties());

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.empty());

        mockitoWhen();

        //when(command.strategyOf(any(TopicOperation.class)))
        //   .thenReturn(strategy);

        when(admin.createPartitions(anyMap()))
            .thenReturn(partitionsResult);

        when(partitionsResult.all())
            .thenReturn(kafkaFuture);

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
    public void should_create_with_default_and_patch_replication_factor_strategies() throws Exception {

        // setup
        final String topic = "03_try_to_create_topic_patch_replication_factor";
        final File directory = new File("./src/test/resources/topics/03_try_to_create_topic_patch_replication_factor/");

        when(backend.latest(any(), anyString())).thenReturn(Optional.empty());

        mockitoWhen();

        command.setDirectory(directory);

        // act
        command.run();

        verify(command, times(2)).strategyOf(topicOperationCaptor.capture());
        var actual = topicOperationCaptor.getAllValues();

        // assert
        assertEquals(2, actual.size());

        var create = actual.get(0);
        assertEquals(topic, create.getTopic());
        assertNull(create.getPartitions());
        assertNull(create.getReplicationFactor());

        var patch = actual.get(1);
        assertEquals(topic, patch.getTopic());
        assertNull(patch.getPartitions());
        assertEquals(2, patch.getReplicationFactor());
    }

    @Test
    public void should_create_patch_and_remove_strategies()
        throws Exception {

        // setup
        final String topic = "08_try_to_create_patch_remove";
        final File directory = new File("./src/test/resources/topics/08_try_to_create_patch_remove/");

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.empty());

        mockitoWhen();

        command.setDirectory(directory);

        // act
        command.run();

        verify(command, times(3)).strategyOf(topicOperationCaptor.capture());
        var actual = topicOperationCaptor.getAllValues();

        // assert
        assertEquals(3, actual.size());

        var create = actual.get(0);
        assertEquals("create", create.getOperation());
        assertEquals(topic, create.getTopic());
        assertNull(create.getPartitions());
        assertNull(create.getReplicationFactor());

        var patch = actual.get(1);
        assertEquals("patch", patch.getOperation());
        assertEquals(topic, patch.getTopic());
        assertNull(patch.getPartitions());
        assertNull(patch.getReplicationFactor());
        assertNotNull(patch.getConfig());
        assertTrue(patch.getConfig().containsKey("compression.type"));

        var remove = actual.get(2);
        assertEquals("remove", remove.getOperation());
        assertEquals(topic, remove.getTopic());
    }

    @Test
    public void should_execute_just_the_newest_migration_strategy() throws Exception {

        // setup
        final String topic = "08_try_to_create_patch_remove";
        final File directory = new File("./src/test/resources/topics/08_try_to_create_patch_remove/");

        final var applied = new MigrationToApply();
        applied.setVersion("v0002");
        final var latest = new Migration();
        latest.setApplied(applied);

        when(backend.latest(any(), anyString()))
            .thenReturn(Optional.of(latest));

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        command.setDirectory(directory);

        // act
        command.run();

        verify(command).strategyOf(topicOperationCaptor.capture());
        var actual = topicOperationCaptor.getAllValues();

        // assert
        assertEquals(1, actual.size());

        var remove = actual.get(0);
        assertEquals("remove", remove.getOperation());
        assertEquals(topic, remove.getTopic());
    }
}
