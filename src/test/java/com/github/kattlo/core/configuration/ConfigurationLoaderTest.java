package com.github.kattlo.core.configuration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;

import com.github.kattlo.core.backend.ResourceType;

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
        assertThat(actual.get(), hasEntry(is("namePattern"), is("^[a-zA-Z0-9\\-]{1,255}$")));
    }

    @Test
    public void should_result_empty_when_no_rules_found() throws IOException {

    }
}
