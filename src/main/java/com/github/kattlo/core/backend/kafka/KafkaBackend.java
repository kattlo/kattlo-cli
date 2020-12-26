package com.github.kattlo.core.backend.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.TopicResourceJoinner;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class KafkaBackend implements Backend {

    static final String TOPIC_T = "__kattlo-topics-state";
    static final String TOPIC_T_HISTORY = "__kattlo-topics-history";

    private static final String CLIENT_ID = "kattlo-cli";
    private static final String GROUP_ID = "kattlo-cli";

    private static final int MAX_ATTEMP_FOR_EMPTY = 3;
    private static final int MAX_POLL_TIME_MS = 800;

    private static final MigrationPartitioner PARTITIONER =
        new MigrationPartitioner();

    private final TopicResourceJoinner topicJoinner =
        new TopicResourceJoinner();

    private boolean initialized = false;
    private Properties configs;

    public KafkaBackend() {
        configs = new Properties();
    }

    public KafkaBackend(final Properties configs){
        this.configs = Objects.requireNonNull(configs);
        this.initialized = true;
    }

    static <T, S extends JsonbSerializer<T>> Producer<String, T>
        producer(Properties configs, Class<S> valueSerializer) {

        var producerConfigs = new Properties();
        producerConfigs.putAll(configs);
        producerConfigs.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());

        producerConfigs.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            valueSerializer.getName());

        producerConfigs.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
            Boolean.TRUE.toString());
        producerConfigs.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerConfigs.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
            "1");
        producerConfigs.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

        producerConfigs.setProperty(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);

        return new KafkaProducer<>(producerConfigs);
    }

    static <T, D extends JsonbDeserializer<T>> Consumer<String, T>
        consumer(Properties configs, Class<D> valueDeserializer) {

        log.debug("Will create Kafka Consumer with base configs {}", configs);

        var consumerConfigs = new Properties();
        consumerConfigs.putAll(configs);
        consumerConfigs.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName());

        consumerConfigs.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            valueDeserializer.getName());

        consumerConfigs.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            Boolean.FALSE.toString());

        consumerConfigs.setProperty(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG,
            Boolean.FALSE.toString());

        consumerConfigs.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG,
            "read_committed");

        consumerConfigs.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        consumerConfigs.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        log.debug("Actual Kafka Consumer configs {}", consumerConfigs);

        return new KafkaConsumer<>(consumerConfigs);
    }

    private ResourceStatus statusFor(Migration applied){
        if(OperationType.CREATE.equals(applied.getOperation())
            || OperationType.PATCH.equals(applied.getOperation())){

                return ResourceStatus.AVAILABLE;

        } else if(OperationType.REMOVE.equals(applied.getOperation())){
            return ResourceStatus.DELETED;
        }

        throw new IllegalArgumentException(applied.getOperation().name());
    }

    private void check() {
        if(!initialized){
            throw new IllegalStateException("Backend did not initialized");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Resource commit(Migration applied) {
        check();
        Objects.requireNonNull(applied);

        var current = current(applied.getResourceType(),
            applied.getResourceName())
            .map(Resource::asMap)
            .orElse(Map.of());
        log.debug("Current resource state {}", current);

        var stateToCommit = topicJoinner.join(current, applied);

        var commit = new ResourceCommit();
        commit.setVersion(applied.getVersion());
        commit.setOperation(applied.getOperation());
        commit.setNotes(applied.getNotes());
        commit.setResourceType(applied.getResourceType());
        commit.setResourceName(applied.getResourceName());
        commit.setTimestamp(applied.getTimestamp());
        commit.setAttributes((Map<String, Object>)
            stateToCommit.get("attributes"));

        var resourceRecord = new ProducerRecord<>(TOPIC_T, applied.key(), commit);
        var historyRecord = new ProducerRecord<>(TOPIC_T_HISTORY, applied.key(), applied);

        try(var resourceProducer = producer(configs, ResourceCommitSerializer.class);
            var historyProducer = producer(configs, MigrationSerializer.class)){

            var resourceFuture = resourceProducer.send(resourceRecord);
            var resourceMetadada = resourceFuture.get();
            log.debug("Resource state produced at {}", resourceMetadada);

            var historyFuture = historyProducer.send(historyRecord);
            var historyMetadata = historyFuture.get();
            log.debug("History entry produced at {}", historyMetadata);

            var newState = new Resource();
            newState.setVersion(applied.getVersion());
            newState.setStatus(statusFor(applied));

            newState.setResourceType(applied.getResourceType());
            newState.setResourceName(applied.getResourceName());
            newState.setTimestamp(applied.getTimestamp());
            newState.setAttributes(Map.copyOf(commit.getAttributes()));

            return newState;

        }catch(InterruptedException | ExecutionException e){
            throw new BackendException(e.getMessage(), e);
        }
    }

    private Optional<ResourceCommit> commitOf(ResourceType type, String name) {
        check();
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);

        Optional<ResourceCommit> result = Optional.empty();
        log.debug("Searching the state for {}, named as {}", type, name);

        var partition = PARTITIONER.partition(type, name);
        log.debug("Partition to seek for the current state of {}-{}: {}",
            type, name, partition);

        try(var consumer = consumer(configs, ResourceCommitDeserializer.class)){
            var tp = Collections.singletonList(
                new TopicPartition(TOPIC_T, partition));

            consumer.assign(tp);
            log.debug("Consumer assigned to {}", consumer.assignment());

            consumer.seekToBeginning(tp);

            final var key = Migration.keyFor(type, name);

            int attempsForEmpty = 0;
            while(!result.isPresent()){

                var records = consumer.poll(Duration.ofMillis(MAX_POLL_TIME_MS));
                if(!records.isEmpty()){

                    result = StreamSupport.stream(records.spliterator(), false)
                        .filter(r -> key.equals(r.key()))
                        .peek(r -> log.debug("Found record {}", r))
                        .map(ConsumerRecord::value)
                        .filter(Objects::nonNull)
                        .peek(r -> log.debug("Current resource commit {}", r))
                        .findFirst();

                    log.trace("Try do commit the higher offsets ...");
                    consumer.commitSync(Duration.ofSeconds(10));
                    log.trace("Offsets committed.");

                } else {
                    if(attempsForEmpty < MAX_ATTEMP_FOR_EMPTY){
                        attempsForEmpty++;
                        log.debug("Attemp for empty # {}", attempsForEmpty);
                    } else {
                        log.debug("Current state does not exists");

                        break;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Optional<Resource> current(ResourceType type, String name) {

        return commitOf(type, name)
                    .map(Resource::from);

    }

    @Override
    public Stream<Migration> history(ResourceType type, String name) {
        check();
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);

        List<Migration> result = new ArrayList<>();
        log.debug("Searching the state for {}, named as {}", type, name);

        var partition = PARTITIONER.partition(type, name);
        log.debug("Partition to seek for the history of {}-{}: {}",
            type, name, partition);

        try(var consumer = consumer(configs, MigrationDeserializer.class)){
            var tp = Collections.singletonList(
                new TopicPartition(TOPIC_T_HISTORY, partition));

            consumer.assign(tp);
            log.debug("Consumer assigned to {}", consumer.assignment());

            consumer.seekToBeginning(tp);

            final var key = Migration.keyFor(type, name);

            int attempsForEmpty = 0;
            while(Boolean.TRUE){

                var records = consumer.poll(Duration.ofMillis(MAX_POLL_TIME_MS));
                if(!records.isEmpty()){

                    result.addAll(
                      StreamSupport.stream(records.spliterator(), false)
                        .filter(r -> key.equals(r.key()))
                        .peek(r -> log.debug("Found history record {}", r))
                        .map(ConsumerRecord::value)
                        .filter(Objects::nonNull)
                        .peek(r -> log.debug("History entry {}", r))
                        .collect(Collectors.toList())
                    );

                    log.trace("Try do commit the higher processed offsets ...");
                    consumer.commitSync(Duration.ofSeconds(10));
                    log.trace("Offsets committed.");

                } else {
                    if(attempsForEmpty < MAX_ATTEMP_FOR_EMPTY){
                        attempsForEmpty++;
                        log.debug("Attemp for empty # {}", attempsForEmpty);
                    } else {

                        if(result.isEmpty()){
                            log.debug("History does not exists for {}-{}", type, name);
                        }

                        break;
                    }
                }
            }
        }

        return result.stream();

    }

    @Override
    public void init(Properties properties) {
        Objects.requireNonNull(properties);
        this.configs.putAll(properties);
        this.initialized = true;
        log.debug("Backend initialized with configs {}", this.configs);
    }
}
