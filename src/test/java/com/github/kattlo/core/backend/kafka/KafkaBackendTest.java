package com.github.kattlo.core.backend.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Migration2;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.file.yaml.model.topic.Original;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author fabiojose
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class KafkaBackendTest {

    @Spy
    private MockProducer<String, ResourceCommit> producer =
        new MockProducer<>(true, null, null);

    @Spy
    private MockConsumer<String, ResourceCommit> consumer =
        new MockConsumer<>(OffsetResetStrategy.EARLIEST);

    @Mock
    private Future<RecordMetadata> future;

    @Spy
    private KafkaBackend backend = new KafkaBackend(new Properties());


    MigrationPartitioner partitioner = new MigrationPartitioner();

    @AfterEach
    public void afterEach(){
        producer.clear();
    }

    private void setupConsumer(ConsumerRecord<String, ResourceCommit> record){

        consumer.schedulePollTask(() -> {
            var tp = new TopicPartition(KafkaBackend.TOPIC_T,
                partitioner.partition(record.value().getResourceType(),
                    record.value().getResourceName()));

            consumer.updateBeginningOffsets(Map.of(tp, Long.valueOf(0)));
            consumer.addRecord(record);
        });

    }

    private void setupConsumer(ResourceType type, String name) {

        var tp = new TopicPartition(KafkaBackend.TOPIC_T,
            partitioner.partition(type, name));

        consumer.schedulePollTask(() -> {
            //consumer.rebalance(Collections.singletonList(tp));
            consumer.updateBeginningOffsets(Map.of(tp, Long.valueOf(0)));
            //System.out.println(consumer.assignment());
            //consumer.addRecord(record);
            //consumer.seek(tp, 0);
        });

    }

    @Test
    public void should_commit_with_right_topic_resource_partition_key() throws Exception {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(1, records.size());

            var actual = records.iterator().next();

            assertEquals(applied.key(), actual.key());
        }

    }

    @Test
    public void should_commit_with_right_topic_resource_topic_name() {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(1, records.size());

            var actual = records.iterator().next();

            assertEquals(KafkaBackend.TOPIC_T, actual.topic());
        }

    }
    @Test
    public void should_commit_the_topic_resource_applied_migration() {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(1, records.size());

            var record = records.iterator().next();
            var actual = record.value();

            assertEquals("topic-name-1", actual.getResourceName());
            assertEquals(ResourceType.TOPIC, actual.getResourceType());
            assertEquals("v0001", actual.getVersion());
            assertEquals("some notes", actual.getNotes());
            assertEquals(OperationType.CREATE, actual.getOperation());
            assertEquals("topic-name-1", actual.getResourceName());
            assertEquals(ResourceType.TOPIC, actual.getResourceType());
            assertNotNull(actual.getTimestamp());

            var actualAttributes = actual.getAttributes();
            assertNotNull(actualAttributes);
            assertEquals("2", actualAttributes.get("partitions"));
            assertEquals("1", actualAttributes.get("replicationFactor"));

            var actualConfig = (Map<String, Object>)actualAttributes.get("config");
            assertNotNull(actualConfig);
            assertEquals("snappy", actualConfig.get("compression.type"));

            var actualHistories = actual.getHistory();
            assertEquals(1, actualHistories.size());

            var actualHistory = actualHistories.iterator().next();
            assertEquals(applied.asMigrationMap(), actualHistory);
        }
    }

    @Test
    public void should_throw_when_the_topic_resource_commit_fails() throws Exception {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            setupConsumer(ResourceType.TOPIC, topic);

            doReturn(future)
                .when(producer).send(any());

            when(future.get())
                .thenThrow(new InterruptedException("failure"));

            // act & assert
            assertThrows(BackendException.class, () ->
                backend.commit(applied));
        }
    }

    @Test
    public void should_return_the_new_topic_resource_state() {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            var actual = backend.commit(applied);

            // assert
            assertNotNull(actual);

            assertEquals(applied.getVersion(), actual.getVersion());
            assertEquals(ResourceStatus.AVAILABLE, actual.getStatus());
            assertEquals(ResourceType.TOPIC, actual.getResourceType());
            assertEquals(applied.getResourceName(), actual.getResourceName());
            assertEquals(applied.getTimestamp(), actual.getTimestamp());
            assertEquals(applied.getAttributes(), actual.getAttributes());
        }

    }

    @Test
    public void should_return_the_current_topic_resource_state() {

        // setup
        var topic = "topic-name-1";

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        var commit = ResourceCommit.from(applied);
        commit.setAttributes(Map.copyOf(applied.getAttributes()));

        ConsumerRecord<String, ResourceCommit> record =
            new ConsumerRecord<>(KafkaBackend.TOPIC_T,
                partitioner.partition(applied.getResourceType(), applied.getResourceName()),
                0, applied.key(), commit);

        setupConsumer(record);

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.consumer(any()))
                .thenReturn(consumer);

            // act
            var actual = backend.current(ResourceType.TOPIC, topic);

            // assert
            assertTrue(actual.isPresent());
            actual.ifPresent(current -> {
                assertEquals(applied.getVersion(), current.getVersion());
                assertEquals(ResourceStatus.AVAILABLE, current.getStatus());
                assertEquals(ResourceType.TOPIC, current.getResourceType());
                assertEquals(topic, current.getResourceName());
                assertEquals(applied.getTimestamp(), current.getTimestamp());
                assertEquals(applied.getAttributes(), current.getAttributes());
            });
        }

    }

    @Test
    public void should_return_the_new_joined_topic_resource_state() {

    }

    @Test
    public void should_throw_when_fail_to_fetch_the_current_resource_state() {

    }

    @Test
    public void should_return_empty_when_current_topic_state_not_found() {

    }

    @Test
    public void should_return_the_history_of_topic_migrations() {

    }

    @Test
    public void should_return_empty_when_does_not_have_topic_history() {

    }
}
