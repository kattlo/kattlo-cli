package com.github.kattlo.core.kafka;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.exception.TopicDescriptionException;
import com.github.kattlo.topic.TopicUtils;
import com.github.kattlo.topic.migration.Strategy;
import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class Kafka {

    private static final String INITIAL_VERSION = "v0001";
    private static final String INITIAL_NOTES = "Kattlo topic to manage topics migrations";
    private static final String NO_FILE = ".";

    static final Integer TOPIC_T_DEFAULT_REPLICATION_FACTOR = 1;
    static final Integer TOPIC_T_DESIRED_REPLICATION_FACTOR = 2;
    static final Integer TOPIC_T_PARTITIONS = 50;
    static final String TOPIC_T_STATE = "__kattlo-topics-state";
    static final Map<String, Object> TOPIC_T_STATE_CONFIG = Map.of(
        "cleanup.policy", "compact",
        "delete.retention.ms", "0",
        "max.compaction.lag.ms", "600000",
        "max.message.bytes", "1048588",
        "message.timestamp.type", "CreateTime",
        "min.cleanable.dirty.ratio", "0.0001",
        "min.compaction.lag.ms", "0",
        "retention.bytes", "-1",
        "segment.bytes", "104857600",
        "segment.ms", "3000"
    );

    static final String TOPIC_T_HISTORY = "__kattlo-topics-history";
    static final Map<String, Object> TOPIC_T_HISTORY_CONFIG = Map.of(
        "cleanup.policy", "delete",
        "delete.retention.ms", "0",
        "max.message.bytes", "1048588",
        "message.timestamp.type", "CreateTime",
        "min.cleanable.dirty.ratio", "0.0001",
        "retention.ms", "-1",
        "segment.bytes", "104857600"
    );

    public AdminClient adminFor(Properties configs) {
        return AdminClient.create(configs);
    }

    Integer replicationFactor(AdminClient admin)
            throws InterruptedException, ExecutionException {

        var cluster = admin.describeCluster();

        var nodes = cluster.nodes().get();
        log.debug("The number of cluster nodes is {}", nodes.size());

        if(nodes.size() >= TOPIC_T_DESIRED_REPLICATION_FACTOR){
            return TOPIC_T_DESIRED_REPLICATION_FACTOR;
        }

        return TOPIC_T_DEFAULT_REPLICATION_FACTOR;
    }

    String topicFor(final String name, Integer partitions,
            Map<String, Object> config, Properties adminConfigs) {

        Objects.requireNonNull(name, "Provide a not null topic name");
        Objects.requireNonNull(partitions, "Provide a not null paritions value");
        Objects.requireNonNull(config, "Provide a not null config map");
        Objects.requireNonNull(adminConfigs, "Provider a not null configs for AdminClient");

        var admin = adminFor(adminConfigs);

        try {
            var found = TopicUtils.describe(name, admin);

            if(found.isEmpty()){
                log.debug("Topic not found {}", name);

                var operation = TopicOperation.builder()
                    .version(INITIAL_VERSION)
                    .operation(OperationType.CREATE.name().toLowerCase())
                    .notes(INITIAL_NOTES)
                    .topic(name)
                    .partitions(partitions)
                    .replicationFactor(replicationFactor(admin))
                    .config(config)
                    .file(Path.of(NO_FILE))
                    .config(Map.copyOf(config))
                    .build();

                var create = Strategy.of(operation);

                // create the topic
                create.execute(admin);

            } else {
                log.debug("Found topic description {}", found.get());
                return found.get().name();
            }

        }catch(InterruptedException | ExecutionException e){
            throw new TopicDescriptionException(e.getMessage(), e);
        }

        return name;
    }

    public String topicsStateTopicName(Properties configs) {
        return topicFor(TOPIC_T_STATE, TOPIC_T_PARTITIONS,
            TOPIC_T_STATE_CONFIG, configs);
    }

    public String topicsHistoryTopicName(Properties configs) {
        return topicFor(TOPIC_T_HISTORY, TOPIC_T_PARTITIONS,
            TOPIC_T_HISTORY_CONFIG, configs);
    }
}
