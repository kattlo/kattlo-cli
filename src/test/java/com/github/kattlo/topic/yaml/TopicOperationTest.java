package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TopicOperationTest {

    @Test
    public void should_throw_illegal_when_version_is_blank() {

        // setup
        var create = TopicOperation.builder()
            .version("  ")
            .operation("create")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_partitions_is_invalid(){

        // setup
        var create = TopicOperation.builder()
            .version("v0000")
            .operation("create")
            .notes("notes")
            .topic("topic")
            .partitions(0)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_replication_factor_is_invalid() {

        var create = TopicOperation.builder()
            .version("v0000")
            .operation("create")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(0)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_topic_is_blank() {

        var create = TopicOperation.builder()
            .version("v0000")
            .operation("create")
            .notes("notes")
            .topic("  ")
            .partitions(1)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_operation_is_blank() {

        var create = TopicOperation.builder()
            .version("v0000")
            .operation(null)
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_operation_is_invalid() {

        var create = TopicOperation.builder()
            .version("v0000")
            .operation("illegal")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }
}
