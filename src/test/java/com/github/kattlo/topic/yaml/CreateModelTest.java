package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CreateModelTest {

    @Test
    public void should_throw_illegal_when_partitions_is_invalid(){

        // setup
        var create = CreateModel.builder()
            .notes("notes")
            .topic("topic")
            .partitions(0)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_replication_factor_is_invalid() {

        var create = CreateModel.builder()
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(0)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }

    @Test
    public void should_throw_illegal_when_topic_is_blank() {

        var create = CreateModel.builder()
            .notes("notes")
            .topic("  ")
            .partitions(1)
            .replicationFactor(1)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }
}
