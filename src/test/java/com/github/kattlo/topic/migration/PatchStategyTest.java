package com.github.kattlo.topic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.AlterConfigOp.OpType;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author fabiojose
 */
@ExtendWith(MockitoExtension.class)
public class PatchStategyTest {

    @Mock
    AdminClient admin;

    @Mock
    CreatePartitionsResult partitionsResult;

    @Mock
    AlterConfigsResult configsResult;

    @Mock
    KafkaFuture<Void> partitionsResultFuture;

    @Mock
    KafkaFuture<Void> configsResultFuture;

    @Mock
    DescribeTopicsResult describeTopicsResult;

    @Mock
    KafkaFuture<Map<String, TopicDescription>> describeTopicsResultFuture;

    @Captor
    ArgumentCaptor<Map<String, NewPartitions>> newPartitionsCaptor;

    @Captor
    ArgumentCaptor<Map<ConfigResource, Collection<AlterConfigOp>>> newConfigCaptor;

    private void describePartitionsMockitoWhen() throws Exception {

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));
        nodes.add(new Node(7, "nodeC", 9092));

        var tp0 = new TopicPartitionInfo(0, nodes.get(0), nodes.subList(0, 2), nodes.subList(0, 2));
        var tp1 = new TopicPartitionInfo(1, nodes.get(1), nodes.subList(0, 2), nodes.subList(0, 2));

        var description = new TopicDescription("topic", false,
            List.of(tp0, tp1));

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        when(describeTopicsResultFuture.get())
            .thenReturn(Map.of("topic", description));
    }

    private void partitionsMockitoWhen() throws Exception {

        when(admin.createPartitions(anyMap()))
            .thenReturn(partitionsResult);

        when(partitionsResult.all())
            .thenReturn(partitionsResultFuture);

        when(partitionsResultFuture.get())
            .thenReturn((Void)null);

        describePartitionsMockitoWhen();
    }

    private void configMockitoWhen() throws Exception {

        when(admin.incrementalAlterConfigs(anyMap()))
            .thenReturn(configsResult);

        when(configsResult.all())
            .thenReturn(configsResultFuture);

        when(configsResultFuture.get())
            .thenReturn((Void)null);

        describePartitionsMockitoWhen();
    }

    @Test
    public void should_patch_partitions() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .partitions(3)
            .build();

        var patch = Strategy.of(operation);

        partitionsMockitoWhen();

        // act
        patch.execute(admin);

        verify(admin).createPartitions(newPartitionsCaptor.capture());
        var captured = newPartitionsCaptor.getValue();

        var actual = captured.get(operation.getTopic());

        //assert
        assertNotNull(actual);
        assertEquals(operation.getPartitions(), actual.totalCount());
    }


    @Test
    public void should_patch_and_set_new_config() throws Exception {

        // setup
        var config = Map.of("compression.type", (Object)"snappy");

        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .config(config)
            .build();

        var patch = Strategy.of(operation);

        configMockitoWhen();

        // act
        patch.execute(admin);

        verify(admin).incrementalAlterConfigs(newConfigCaptor.capture());
        var captured = newConfigCaptor.getValue();

        var resource = new ConfigResource(Type.TOPIC, operation.getTopic());
        var actual = captured.get(resource);

        //assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(
            config.get("compression.type"),
            actual.iterator().next().configEntry().value());
    }

    @Test
    public void should_patch_config_to_cluster_default() throws Exception {

        // setup
        var config = Map.of("compression.type", (Object)"$default");

        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .config(config)
            .build();

        var patch = Strategy.of(operation);

        configMockitoWhen();

        // act
        patch.execute(admin);

        verify(admin).incrementalAlterConfigs(newConfigCaptor.capture());
        var captured = newConfigCaptor.getValue();

        var resource = new ConfigResource(Type.TOPIC, operation.getTopic());
        var actual = captured.get(resource);

        //assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        var op = actual.iterator().next();
        assertEquals(OpType.DELETE, op.opType());
        assertEquals("compression.type", op.configEntry().name());
    }

    @Test
    public void should_fail_when_exception_to_patch_partitions() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .partitions(3)
            .build();

        var patch = Strategy.of(operation);

        partitionsMockitoWhen();

        when(partitionsResultFuture.get())
            .thenThrow(new InterruptedException("failure"));

        // assert
        assertThrows(TopicPatchException.class, () ->
            patch.execute(admin));

    }

    @Test
    public void should_fail_when_exception_to_patch_config() throws Exception {

        // setup
        var config = Map.of("compression.type", (Object)"snappy");

        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .config(config)
            .build();

        var patch = Strategy.of(operation);

        describePartitionsMockitoWhen();

        when(admin.incrementalAlterConfigs(anyMap()))
            .thenReturn(configsResult);

        when(configsResult.all())
            .thenReturn(configsResultFuture);

        when(configsResultFuture.get())
            .thenThrow(new InterruptedException("failure"));

        //assert.
        assertThrows(TopicPatchException.class, () ->
            patch.execute(admin));
    }

    @Test
    public void should_fail_when_topic_does_not_exists() throws Exception {
        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("-no-exists-")
            .partitions(4)
            .build();

        var patch = Strategy.of(operation);

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        when(describeTopicsResultFuture.get())
            .thenReturn(Map.of());

        // act & assert
        var actual =
        assertThrows(TopicPatchException.class, () ->
            patch.execute(admin));

        assertTrue(actual.getMessage().contains("topic does not exists"));
    }
}
