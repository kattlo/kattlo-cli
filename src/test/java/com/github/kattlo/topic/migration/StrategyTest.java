package com.github.kattlo.topic.migration;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * @author fabiojose
 */
public class StrategyTest {

    @Test
    public void should_result_create_strategy_for_create_operation() {

        var operation = TopicOperation.builder()
            .version("v0000")
            .operation("create")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .build();

        Strategy actual = Strategy.of(operation);

        MatcherAssert.assertThat(actual,
            CoreMatchers.instanceOf(CreateStrategy.class));
    }

    @Test
    public void should_result_patch_strategy_for_patch_operation() {

        var operation = TopicOperation.builder()
            .version("v0000")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .build();

        Strategy actual = Strategy.of(operation);

        MatcherAssert.assertThat(actual,
            CoreMatchers.instanceOf(PatchStrategy.class));
    }

    @Test
    public void should_result_remove_stategy_for_remove_operation() {

        var operation = TopicOperation.builder()
            .version("v0000")
            .operation("remove")
            .notes("notes")
            .topic("topic")
            .partitions(1)
            .replicationFactor(1)
            .build();

        Strategy actual = Strategy.of(operation);

        MatcherAssert.assertThat(actual,
            CoreMatchers.instanceOf(RemoveStrategy.class));
    }
}
