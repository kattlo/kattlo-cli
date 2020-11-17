package com.github.kattlo.core.backend.file.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import com.github.kattlo.core.exception.LoadException;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * @author fabiojose
 */
public class LoaderTest {

    @Test
    public void should_load_the_state_file_from_directory() {

        var actual = Loader.load(Path.of("./src/test/resources/backend/file/.kattlo-topics.yaml"));

        assertEquals(1, actual.getTopics().size());

        var current =
            actual.getTopics().iterator().next();

        assertEquals("topic-name", current.getTopic());
    }

    @Test
    public void should_throw_when_file_does_not_exists() {

        var file = Path.of("/file/not/found.yaml");

        assertThrows(LoadException.class, () ->
            Loader.load(file));

    }

    @Test
    public void should_throw_when_migration_is_an_invalid_yaml() {

        var file = Path.of("./src/test/resources/backend/file/.invalid-kattlo-topics.yaml");

        assertThrows(YAMLException.class, () ->
            Loader.load(file));
    }

}
