package com.github.kattlo.core.backend.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.github.kattlo.core.exception.TopicDescriptionException;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.topic.TopicUtils;
import com.github.kattlo.topic.migration.TopicCreateException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class KafkaBackendConfigTest {

    @Mock
    private Kafka kafka;

    @Mock
    private AdminClient admin;

    @Mock
    CreateTopicsResult result;

    @Mock
    KafkaFuture<Void> future;

    @Mock
    private DescribeClusterResult describeClusterResult;

    @Mock
    private KafkaFuture<Collection<Node>> describeClusterResultFuture;

    @Captor
    private ArgumentCaptor<Collection<NewTopic>> newTopicCaptor;

    @InjectMocks
    private KafkaBackendConfig backendConfig;

    private void mockitoWhen() throws Exception {

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        when(admin.createTopics(anyCollection()))
            .thenReturn(result);

        when(result.all())
            .thenReturn(future);

        when(future.get())
            .thenReturn((Void)null);

        var nodes = new ArrayList<Node>();
        nodes.add(new Node(9, "nodeA", 9092));
        nodes.add(new Node(5, "nodeB", 9092));

        when(admin.describeCluster())
            .thenReturn(describeClusterResult);

        when(describeClusterResult.nodes())
            .thenReturn(describeClusterResultFuture);

        when(describeClusterResultFuture.get())
            .thenReturn(nodes);
    }

    @Test
    public void should_fail_when_cant_describe_the_topic_name() {

        var configs = new Properties();

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenThrow(new InterruptedException("failure"));

            assertThrows(TopicDescriptionException.class, ()->
                backendConfig.topicFor("__kattlo-topics-state", 1, Map.of(), configs));

        }
    }


    @Test
    public void should_result_the_topic_name_when_found() {

        var configs = new Properties();

        var nodes = List.of(
            new Node(0, "localhost", 9092)
        );

        var partition = new TopicPartitionInfo(0, nodes.get(0), nodes, nodes);

        var topic = "__kattlo-topics-state";
        var description = new TopicDescription(topic, false, List.of(partition));

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.of(description));

            var actual = backendConfig.topicFor(topic, 1, Map.of(), configs);

            assertEquals(topic, actual);
        }
    }

    @Test
    public void should_create_the_topic_when_not_found() throws Exception {

        var configs = new Properties();

        var topic = "__kattlo-topics-state";
        var partitions = 3;
        Map<String, Object> config = Map.of(
            "retention.ms", "-1",
            "delete.retention.ms", "0"
        );

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.empty());

            mockitoWhen();

            backendConfig.topicFor(topic, partitions, config, configs);

            verify(admin).createTopics(newTopicCaptor.capture());
            var actual = newTopicCaptor.getValue();

            assertEquals(1, actual.size());

            var newTopic = actual.iterator().next();
            assertEquals(topic, newTopic.name());
            assertEquals(partitions, newTopic.numPartitions());
            assertEquals(config, newTopic.configs());
        }
    }

    @Test
    public void should_fail_when_cant_create_the_topic_name() throws Exception {

        var configs = new Properties();

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.empty());

            mockitoWhen();

            when(future.get())
                .thenThrow(new InterruptedException("failure"));

            assertThrows(TopicCreateException.class, ()->
                backendConfig.topicFor("__kattlo-topics-state", 1, Map.of(), configs));

        }
    }

    @Test
    public void should_create_topics_state_with_right_config() throws Exception {

        var configs = new Properties();

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.empty());

            mockitoWhen();

            backendConfig.topicsStateTopicName(configs);

            verify(admin).createTopics(newTopicCaptor.capture());
            var actual = newTopicCaptor.getValue();

            assertEquals(1, actual.size());

            var newTopic = actual.iterator().next();
            assertEquals(KafkaBackendConfig.TOPIC_T_PARTITIONS, newTopic.numPartitions());
            assertEquals(KafkaBackendConfig.TOPIC_T_STATE_CONFIG, newTopic.configs());
        }
    }

    @Test
    public void should_create_topics_history_with_right_config() throws Exception {

        var configs = new Properties();

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.empty());

            mockitoWhen();

            backendConfig.topicsHistoryTopicName(configs);

            verify(admin).createTopics(newTopicCaptor.capture());
            var actual = newTopicCaptor.getValue();

            assertEquals(1, actual.size());

            var newTopic = actual.iterator().next();
            assertEquals(KafkaBackendConfig.TOPIC_T_PARTITIONS, newTopic.numPartitions());
            assertEquals(KafkaBackendConfig.TOPIC_T_HISTORY_CONFIG, newTopic.configs());
        }
    }

    @Test
    public void should_create_the_topic_with_replication_factor_of_two_when_possible() throws Exception {

        var configs = new Properties();

        try(var mocked = mockStatic(TopicUtils.class)){
            mocked.when(() -> TopicUtils.describe(anyString(), any()))
                .thenReturn(Optional.empty());

            mockitoWhen();

            backendConfig.topicsHistoryTopicName(configs);

            verify(admin).createTopics(newTopicCaptor.capture());
            var actual = newTopicCaptor.getValue();

            assertEquals(1, actual.size());

            var newTopic = actual.iterator().next();
            assertEquals(KafkaBackendConfig.TOPIC_T_DESIRED_REPLICATION_FACTOR, newTopic.replicationFactor());
        }
    }
}
