package com.github.kattlo.topic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
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
public class RemoveStrategyTest {

    @Mock
    private AdminClient admin;

    @Mock
    private DescribeTopicsResult describeTopicsResult;

    @Mock
    private KafkaFuture<Map<String,TopicDescription>> describeTopicsResultFuture;

    @Mock
    private DeleteTopicsResult deleteTopicsResult;

    @Mock
    private KafkaFuture<Void> deleteTopicsResultFuture;

    @Captor
    private ArgumentCaptor<Collection<String>> deleteTopicsCaptor;

    @Test
    public void should_remove_the_topic() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("remove")
            .notes("notes")
            .topic("topic")
            .build();

        var remove = Strategy.of(operation);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));
        nodes.add(new Node(7, "nodeC", 9092));

        var tp0 = new TopicPartitionInfo(0, nodes.get(0), nodes.subList(0, 2), nodes.subList(0, 2));
        var tp1 = new TopicPartitionInfo(1, nodes.get(1), nodes.subList(0, 2), nodes.subList(0, 2));

        var description = new TopicDescription("topic", false,
            List.of(tp0, tp1));

        when(admin.deleteTopics(anyCollection()))
            .thenReturn(deleteTopicsResult);

        when(deleteTopicsResult.all())
            .thenReturn(deleteTopicsResultFuture);

        when(deleteTopicsResultFuture.get())
            .thenReturn((Void)null);

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        when(describeTopicsResultFuture.get())
            .thenReturn(Map.of("topic", description));

        // act
        remove.execute(admin);

        verify(admin).deleteTopics(deleteTopicsCaptor.capture());
        var actual = deleteTopicsCaptor.getValue();

        // assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(operation.getTopic()));
    }

    @Test
    public void should_fail_topic_does_not_exists() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("remove")
            .notes("notes")
            .topic("-no-exists-")
            .build();

        var remove = Strategy.of(operation);

        when(admin.describeTopics(anyCollection()))
            .thenReturn(describeTopicsResult);

        when(describeTopicsResult.all())
            .thenReturn(describeTopicsResultFuture);

        when(describeTopicsResultFuture.get())
            .thenReturn(Map.of());

        // act & assert
        var actual =
        assertThrows(TopicRemoveException.class, () ->
            remove.execute(admin));

        assertTrue(actual.getMessage().contains("topic does not exists"));
    }
}
