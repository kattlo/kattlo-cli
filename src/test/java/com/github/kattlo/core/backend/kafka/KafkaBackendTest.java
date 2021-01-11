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
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.Original;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private MockProducer<String, Object> producer =
        new MockProducer<>(true, null, null);

    @Spy
    private MockConsumer<String, ResourceCommit> consumer =
        new MockConsumer<>(OffsetResetStrategy.EARLIEST);

    @Spy
    private MockConsumer<String, Migration> migrationConsumer =
        new MockConsumer<>(OffsetResetStrategy.EARLIEST);

    @Mock
    private Future<RecordMetadata> future;

    @Mock
    private KafkaBackendConfig backendConfig;

    @Spy
    @InjectMocks
    private KafkaBackend backend = new KafkaBackend(new Properties());

    private MigrationPartitioner partitioner = new MigrationPartitioner();

    @AfterEach
    public void afterEach(){
        producer.clear();
    }

    private void setupConsumer(ConsumerRecord<String, ResourceCommit> record){

        consumer.schedulePollTask(() -> {
            var tp = new TopicPartition(KafkaBackendConfig.TOPIC_T_STATE,
                partitioner.partition(record.value().getResourceType(),
                    record.value().getResourceName()));

            consumer.updateBeginningOffsets(Map.of(tp, Long.valueOf(0)));
            consumer.addRecord(record);
        });

    }

    private void setupConsumer(ConsumerRecord<String, Migration> record, String topic){

        migrationConsumer.schedulePollTask(() -> {
            var tp = new TopicPartition(topic,
                partitioner.partition(record.value().getResourceType(),
                    record.value().getResourceName()));

            migrationConsumer.updateBeginningOffsets(Map.of(tp, Long.valueOf(0)));
            migrationConsumer.addRecord(record);
        });

    }

    private void setupConsumer(ResourceType type, String name) {

        var tp = new TopicPartition(KafkaBackendConfig.TOPIC_T_STATE,
            partitioner.partition(type, name));

        consumer.schedulePollTask(() -> {
            //consumer.rebalance(Collections.singletonList(tp));
            consumer.updateBeginningOffsets(Map.of(tp, Long.valueOf(0)));
            //consumer.addRecord(record);
            //consumer.seek(tp, 0);
        });

        var tpHistory = new TopicPartition(KafkaBackendConfig.TOPIC_T_HISTORY,
            partitioner.partition(type, name));

        migrationConsumer.schedulePollTask(() -> {
            migrationConsumer.updateBeginningOffsets(Map.of(tpHistory, Long.valueOf(0)));
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

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(2, records.size());

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

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(2, records.size());

            var actual = records.iterator().next();

            assertEquals(KafkaBackendConfig.TOPIC_T_STATE, actual.topic());
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

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(2, records.size());

            var record = records.iterator().next();
            var actual = (ResourceCommit)record.value();

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

        }
    }

    @Test
    public void should_commit_with_right_topic_history_topic_name() {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(2, records.size());

            var actual = records.get(1);

            assertEquals(KafkaBackendConfig.TOPIC_T_HISTORY, actual.topic());
        }

    }
    @Test
    public void should_commit_the_new_entry_to_the_topic_history() {

        // setup
        var topic = "topic-name-1";
        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            backend.commit(applied);

            // assert
            var records = producer.history();
            assertEquals(2, records.size());

            var record = records.get(1);
            var actual = (Migration)record.value();

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

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

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

        var applied = new Migration();
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
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

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

        var applied = new Migration();
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

        var record =
            new ConsumerRecord<>(KafkaBackendConfig.TOPIC_T_STATE,
                partitioner.partition(applied.getResourceType(), applied.getResourceName()),
                0, applied.key(), commit);

        when(backendConfig.topicsStateTopicName(any()))
            .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

        setupConsumer(record);

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.consumer(any(), any()))
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

        // setup
        var topic = "topic-name-5";

        var expectedConfig = Map.of(
            "compression.type", "snappy",
            "retention.ms", "-1"
        );

        var expectedAttributes =
            Map.of(
                "partitions", "4",
                "replicationFactor", "2",
                "config", expectedConfig
            );

        var expected = new Resource();
        expected.setVersion("v0002");
        expected.setStatus(ResourceStatus.AVAILABLE);
        expected.setResourceType(ResourceType.TOPIC);
        expected.setResourceName(topic);
        expected.setTimestamp(LocalDateTime.now());
        expected.setAttributes(expectedAttributes);

        var v0001Original = new Original();
        v0001Original.setContentType("text/yaml");
        v0001Original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        v0001Original.setPath("/path/to/original.yaml");

        var v0001Config = Map.of("compression.type", "snappy");

        var v0001 = new Migration();
        v0001.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", v0001Config
        ));
        v0001.setNotes("some notes");
        v0001.setOperation(OperationType.CREATE);
        v0001.setOriginal(v0001Original);
        v0001.setResourceName(topic);
        v0001.setResourceType(ResourceType.TOPIC);
        v0001.setTimestamp(LocalDateTime.now());
        v0001.setVersion("v0001");

        var v0001Commit = ResourceCommit.from(v0001);
        v0001Commit.setAttributes(Map.copyOf(v0001.getAttributes()));

        var v0001Record =
            new ConsumerRecord<>(KafkaBackendConfig.TOPIC_T_STATE,
                partitioner.partition(v0001.getResourceType(), v0001.getResourceName()),
                0, v0001.key(), v0001Commit);

        when(backendConfig.topicsStateTopicName(any()))
            .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

        when(backendConfig.topicsHistoryTopicName(any()))
            .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

        setupConsumer(v0001Record);

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("retention.ms", "-1");

        var applied = new Migration();
        applied.setAttributes(Map.of(
            "partitions", "4",
            "replicationFactor", "2",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName(topic);
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0002");

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.producer(any(), any()))
                .thenReturn(producer);

            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            // act
            var actual = backend.commit(applied);

            // assert
            assertEquals(expected.getVersion(), actual.getVersion());
            assertEquals(expected.getStatus(), actual.getStatus());
            assertEquals(expected.getResourceType(), actual.getResourceType());
            assertEquals(expected.getResourceName(), actual.getResourceName());
            assertEquals(expected.getAttributes(), actual.getAttributes());
        }
    }

    @Test
    public void should_return_empty_when_current_topic_state_not_found() {

        var topic = "not-found";
        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(consumer);

            when(backendConfig.topicsStateTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_STATE);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            var actual = backend.current(ResourceType.TOPIC, topic);

            // assert
            assertTrue(actual.isEmpty());
        }
    }

    @Test
    public void should_return_the_history_of_topic_migrations() {

        var topic = "topic-name-1";

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration();
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
        //commit.getHistory().add(applied.asMigrationMap());

        var record =
            new ConsumerRecord<>(KafkaBackendConfig.TOPIC_T_HISTORY,
                partitioner.partition(applied.getResourceType(), applied.getResourceName()),
                0, applied.key(), applied);

        when(backendConfig.topicsHistoryTopicName(any()))
            .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

        setupConsumer(record, KafkaBackendConfig.TOPIC_T_HISTORY);

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(migrationConsumer);

            // act
            var actualStream = backend.history(ResourceType.TOPIC, topic);
            var actualList = actualStream.collect(Collectors.toList());

            // assert
            assertEquals(1, actualList.size());
            var actual = actualList.iterator().next();

            assertEquals(applied, actual);
        }
    }

    @Test
    public void should_return_empty_when_does_not_have_topic_history() {

        var topic = "not-found";

        try(var mocked = mockStatic(KafkaBackend.class)){
            mocked.when(() -> KafkaBackend.consumer(any(), any()))
                .thenReturn(migrationConsumer);

            when(backendConfig.topicsHistoryTopicName(any()))
                .thenReturn(KafkaBackendConfig.TOPIC_T_HISTORY);

            setupConsumer(ResourceType.TOPIC, topic);

            // act
            var actualStream = backend.history(ResourceType.TOPIC, topic);
            var actualList = actualStream.collect(Collectors.toList());

            // assert
            assertEquals(0, actualList.size());

        }
    }
}
