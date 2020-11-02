package com.github.kattlo.topic.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterPartitionReassignmentsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewPartitionReassignment;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

/**
 * @author fabiojose
 */
@ExtendWith(MockitoExtension.class)
public class PatchStrategyReplicationFactorTest {

    @Mock
    private AdminClient admin;

    @Mock
    private DescribeClusterResult describeClusterResult;

    @Mock
    private KafkaFuture<Collection<Node>> describeClusterResultFuture;

    @Mock
    private AlterPartitionReassignmentsResult replicationFactorResult;

    @Mock
    private KafkaFuture<Void> replicationFactorResultFuture;

    @Mock
    private DescribeTopicsResult describeTopicsResult;

    @Mock
    private KafkaFuture<Map<String,TopicDescription>> describeTopicsResultFuture;

    @Captor
    private ArgumentCaptor<Map<TopicPartition,Optional<NewPartitionReassignment>>> captor;

    @Mock
    private Node broker;

    @Test
    public void should_throws_when_replication_is_greater_than_number_of_brokers()
            throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .replicationFactor(3)
            .build();

        var patch = Strategy.of(operation);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));

        when(admin.describeCluster())
            .thenReturn(describeClusterResult);

        when(describeClusterResult.nodes())
            .thenReturn(describeClusterResultFuture);

        when(describeClusterResultFuture.get())
            .thenReturn(nodes);

        // act
        var actual =
            assertThrows(TopicPatchException.class, () -> patch.execute(admin));

        assertTrue(actual.getMessage().contains("replication factor"));
    }

    @Test
    public void should_fail_when_exception_to_patch_replication_factor()
        throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .replicationFactor(2)
            .build();

        var patch = Strategy.of(operation);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));

        when(admin.describeCluster())
            .thenReturn(describeClusterResult);

        when(describeClusterResult.nodes())
            .thenReturn(describeClusterResultFuture);

        when(describeClusterResultFuture.get())
            .thenThrow(new InterruptedException("interrupted"));

        // act
        var actual =
            assertThrows(TopicPatchException.class, () -> patch.execute(admin));

        assertEquals("interrupted", actual.getMessage());
    }

    @Test
    public void should_throw_when_topic_does_not_exists() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .replicationFactor(2)
            .build();

        var patch = Strategy.of(operation);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));
        nodes.add(new Node(7, "nodeC", 9092));

        when(admin.describeCluster())
            .thenReturn(describeClusterResult);

        when(describeClusterResult.nodes())
            .thenReturn(describeClusterResultFuture);

        when(describeClusterResultFuture.get())
            .thenReturn(nodes);

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        when(describeTopicsResultFuture.get())
            .thenThrow(new InterruptedException("topic does not exists"));

        // act
        assertThrows(TopicPatchException.class, () -> patch.execute(admin));

    }

    @Test
    public void should_patch_to_increase_the_replication_factor() throws Exception {
        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .replicationFactor(3)
            .build();

        var patch = Strategy.of(operation);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));
        nodes.add(new Node(7, "nodeC", 9092));

        when(admin.describeCluster())
            .thenReturn(describeClusterResult);

        when(describeClusterResult.nodes())
            .thenReturn(describeClusterResultFuture);

        when(describeClusterResultFuture.get())
            .thenReturn(nodes);

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        var tp0 = new TopicPartitionInfo(0, nodes.get(0), nodes.subList(0, 2), nodes.subList(0, 2));
        var tp1 = new TopicPartitionInfo(1, nodes.get(1), nodes.subList(0, 2), nodes.subList(0, 2));

        var description = new TopicDescription("topic", false,
            List.of(tp0, tp1));

        when(describeTopicsResultFuture.get())
            .thenReturn(Map.of("topic", description));

        // act
        patch.execute(admin);

        verify(admin).alterPartitionReassignments(captor.capture());
        var actual = captor.getValue();

        // assert
        assertEquals(2, actual.size()); //partitions

        var tp0Assignments = actual.get(new TopicPartition("topic", 0));
        assertEquals(3, tp0Assignments.get().targetReplicas().size());

        var tp1Assignments = actual.get(new TopicPartition("topic", 1));
        assertEquals(3, tp1Assignments.get().targetReplicas().size());
    }

    @Test
    public void should_patch_to_decrease_the_replication_factor() {
        //TODO
    }

    @Test
    public void should_throw_when_replication_factor_already_set() {
        //TODO
    }
}
