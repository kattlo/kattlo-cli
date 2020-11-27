package com.github.kattlo.core.backend.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.kattlo.core.backend.ResourceType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MigrationPartitionerTest {

    @Spy
    private MigrationPartitioner partitioner = new MigrationPartitioner();

    @Test
    public void should_return_a_partition_number() {

        // setup
        var type = ResourceType.TOPIC;
        var name = "some-topic-name";

        // act
        var actual = partitioner.partition(type, name);

        // assert
        assertTrue(actual != -1);

    }

    public void should_return_same_partition_for_same_resource() {

        // setup
        var type = ResourceType.TOPIC;
        var name = "some-topic-name";
        var expected = partitioner.partition(type, name);

        // act
        var actual = partitioner.partition(type, name);

        // assert
        assertEquals(expected, actual);

    }
}
