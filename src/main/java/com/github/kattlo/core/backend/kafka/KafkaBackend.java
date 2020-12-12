package com.github.kattlo.core.backend.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.kattlo.core.backend.Backend2;
import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Migration2;
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

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class KafkaBackend implements Backend2 {

    static final String TOPIC_T = "__kattlo-topics-state";

    private static final String CLIENT_ID = "kattlo";

    private static final int MAX_ATTEMP_FOR_EMPTY = 10;

    private static final MigrationPartitioner PARTITIONER =
        new MigrationPartitioner();

    private final TopicResourceJoinner topicJoinner =
        new TopicResourceJoinner();

    private final Properties configs;
    public KafkaBackend(final Properties configs){
        this.configs = Objects.requireNonNull(configs);
    }

    static Producer<String, ResourceCommit> producer(Properties configs) {

        var producerConfigs = new Properties(configs);
        producerConfigs.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());

        producerConfigs.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            ResourceCommitSerializer.class.getName());

        producerConfigs.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
            Boolean.TRUE.toString());
        producerConfigs.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerConfigs.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
        producerConfigs.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");

        producerConfigs.setProperty(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);

        return new KafkaProducer<>(configs);
    }

    static Consumer<String, ResourceCommit> consumer(Properties configs) {

        var consumerConfigs = new Properties(configs);
        consumerConfigs.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName());

        consumerConfigs.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ResourceCommitDeserializer.class.getName());

        consumerConfigs.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            Boolean.FALSE.toString());

        consumerConfigs.setProperty(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG,
            Boolean.FALSE.toString());

        consumerConfigs.setProperty(ConsumerConfig.DEFAULT_ISOLATION_LEVEL,
            "read_committed");

        consumerConfigs.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID);

        return new KafkaConsumer<>(consumerConfigs);
    }

    private ResourceStatus statusFor(Migration2 applied){
        if(OperationType.CREATE.equals(applied.getOperation())
            || OperationType.PATCH.equals(applied.getOperation())){

                return ResourceStatus.AVAILABLE;

        } else if(OperationType.REMOVE.equals(applied.getOperation())){
            return ResourceStatus.DELETED;
        }

        throw new IllegalArgumentException(applied.getOperation().name());
    }


    @Override
    @SuppressWarnings("unchecked")
    public Resource commit(Migration2 applied) {
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

        commit.setHistory((List<Map<String, Object>>)
            stateToCommit.get("history"));

        var record = new ProducerRecord<>(TOPIC_T, applied.key(), commit);

        var producer = producer(configs);
        var future = producer.send(record);

        try {
            future.get();

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

    @Override
    public Optional<Resource> current(ResourceType type, String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);

        Optional<Resource> result = Optional.empty();
        log.debug("Searching the state for {}, named as {}", type, name);

        var partition = PARTITIONER.partition(type, name);
        log.debug("Partition to seek for the current state of {}-{}: {}",
            type, name, partition);

        try(var consumer = consumer(configs)){
            var tp = Collections.singletonList(
                new TopicPartition(TOPIC_T, partition));

            consumer.assign(tp);
            log.debug("Consumer assigned to {}", consumer.assignment());

            consumer.seekToBeginning(tp);

            final var key = Migration2.keyFor(type, name);

            int attempsForEmpty = 0;
            while(!result.isPresent()){

                var records = consumer.poll(Duration.ofMillis(300));
                if(!records.isEmpty()){

                    result = StreamSupport.stream(records.spliterator(), false)
                        .filter(r -> key.equals(r.key()))
                        .peek(r -> log.debug("Found record {}", r))
                        .map(ConsumerRecord::value)
                        .filter(Objects::nonNull)
                        .map(Resource::from)
                        .peek(r -> log.debug("Resulting resource corrent state " + r))
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
    public Stream<Migration2> history(ResourceType type, String name) {
        return null;
    }

}
