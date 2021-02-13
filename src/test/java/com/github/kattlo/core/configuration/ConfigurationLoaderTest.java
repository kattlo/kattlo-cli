package com.github.kattlo.core.configuration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.github.kattlo.core.backend.ResourceType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class ConfigurationLoaderTest {

    @Test
    public void should_throw_when_file_does_not_exist() {

        var file = new File("/path/not/found/.kattlo.yaml");

        assertThrows(IOException.class, () ->
            ConfigurationLoader.load(file, ResourceType.TOPIC));
    }

    @Test
    public void should_load_the_topic_rules_configution() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertTrue(actual.isPresent());
        assertThat(actual.get(), hasEntry(is("namePattern"), is("^[a-z0-9\\-]{1,255}$")));
    }

    @Test
    public void should_result_empty_when_no_rules_found() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_empty.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertFalse(actual.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_the_integer_value() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertTrue(actual.isPresent());
        var config = (Map<String, Object>)actual.get().get("config");
        var maxMessageBytes = (Map<String, Object>)config.get("max.message.bytes");

        var actualValue = maxMessageBytes.get("<=");
        assertThat(actualValue, Matchers.instanceOf(Integer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_the_long_value() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertTrue(actual.isPresent());
        var config = (Map<String, Object>)actual.get().get("config");
        var maxMessageBytes = (Map<String, Object>)config.get("max.compaction.lag.ms");

        var actualValue = maxMessageBytes.get("<=");
        assertThat(actualValue, Matchers.instanceOf(Long.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_double_value() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertTrue(actual.isPresent());
        var config = (Map<String, Object>)actual.get().get("config");
        var maxMessageBytes = (Map<String, Object>)config.get("min.cleanable.dirty.ratio");

        var actualValue = maxMessageBytes.get(">=");
        assertThat(actualValue, Matchers.instanceOf(Double.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_human_readable_values() throws IOException {

        var file = new File("./src/test/resources/topics/rules/.kattlo_human_readable.yaml");

        var actual = ConfigurationLoader.load(file, ResourceType.TOPIC);

        assertTrue(actual.isPresent());
        var config = (Map<String, Object>)actual.get().get("config");
        var maxMessageBytes = (Map<String, Object>)config.get("max.message.bytes");

        var actualValue = maxMessageBytes.get("<=");
        assertThat(actualValue, Matchers.instanceOf(String.class));
    }
}
