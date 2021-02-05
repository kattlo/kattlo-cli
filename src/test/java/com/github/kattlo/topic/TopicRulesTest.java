package com.github.kattlo.topic;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.configuration.ConfigurationLoader;
import com.github.kattlo.core.configuration.condition.ByPass;
import com.github.kattlo.core.configuration.condition.Equals;
import com.github.kattlo.core.configuration.condition.GreaterOrEquals;
import com.github.kattlo.core.configuration.condition.In;
import com.github.kattlo.core.configuration.condition.TextPattern;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class TopicRulesTest {

    @Test
    public void should_throw_when_rules_arg_is_null() {

        assertThrows(NullPointerException.class, () -> TopicRules.of(null));
    }

    @Test
    public void should_has_by_pass_rule_when_not_found() throws IOException {

        var actual = TopicRules.of(Map.of());

        assertThat(actual.getNamePattern(), Matchers.instanceOf(ByPass.class));
        assertThat(actual.getPartitions(), Matchers.instanceOf(ByPass.class));
        assertThat(actual.getReplicationFactor(), Matchers.instanceOf(ByPass.class));
        assertTrue(actual.getConfig().isEmpty());

    }

    @Test
    public void should_has_name_pattern_rule() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic_no_partitions.yaml");
        var rules = ConfigurationLoader.load(file, ResourceType.TOPIC);

        var actual = TopicRules.of(rules.get());

        assertThat(actual.getNamePattern(), Matchers.instanceOf(TextPattern.class));
    }

    @Test
    public void should_has_partitions_rule() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var rules = ConfigurationLoader.load(file, ResourceType.TOPIC);

        var actual = TopicRules.of(rules.get());

        assertThat(actual.getPartitions(), Matchers.instanceOf(GreaterOrEquals.class));
    }

    @Test
    public void should_has_replication_factor_rule() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var rules = ConfigurationLoader.load(file, ResourceType.TOPIC);

        var actual = TopicRules.of(rules.get());

        assertThat(actual.getReplicationFactor(), Matchers.instanceOf(Equals.class));
    }

    @Test
    public void should_has_config_in_rule() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var rules = ConfigurationLoader.load(file, ResourceType.TOPIC);

        var actual = TopicRules.of(rules.get());

        var actualConfig = actual.getConfig();
        var actualCompressionType = actualConfig.get("compression.type");

        assertThat(actualCompressionType, Matchers.instanceOf(In.class));
    }
}
