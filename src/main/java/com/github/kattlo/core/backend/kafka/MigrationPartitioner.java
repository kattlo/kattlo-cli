package com.github.kattlo.core.backend.kafka;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.github.kattlo.core.backend.ResourceType;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class MigrationPartitioner implements Partitioner {

    private static final Object NO_VALUE = null;
    private static final byte[] NO_VALUE_BYTES = null;
    private static final Cluster NO_CLUSTER = null;

    private static final int PARTITIONS = 50;

    private static final Map<ResourceType, String> TOPIC = Map.of(
        ResourceType.TOPIC, KafkaBackend.TOPIC_T
    );

    @Override
    public void configure(Map<String, ?> config) {
    }

    @Override
    public void close() {
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value,
            byte[] valueBytes, Cluster cluster) {
        return Utils.toPositive(Utils.murmur2(keyBytes)) % PARTITIONS;
    }

    public int partition(ResourceType type, String name) {
        final var key = requireNonNull(type.name()) + "_" + requireNonNull(name);
        log.debug("Partition for key {}", key);

        return partition(TOPIC.get(type), key, key.getBytes(), NO_VALUE,
            NO_VALUE_BYTES, NO_CLUSTER);
    }
}
