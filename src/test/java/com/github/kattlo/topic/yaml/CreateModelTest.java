package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CreateModelTest {

    @Test
    public void should_throw_when_partitions_illegal(){

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
    public void should_throw_when_replication_factor_illegal() {

        var create = CreateModel.builder()
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(0)
            .config(null);

        assertThrows(IllegalArgumentException.class, () -> create.build());
    }
}
