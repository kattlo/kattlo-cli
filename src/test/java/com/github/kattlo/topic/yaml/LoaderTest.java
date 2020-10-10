package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import com.github.kattlo.topic.yaml.Loader.Model;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

public class LoaderTest {

    @Test
    public void should_result_false_when_file_name_does_not_match() {

        final String fileName = "v001_my-migration.yaml";

        assertFalse(Loader.FILE_NAME_PATTERN.matcher(fileName).matches());
    }

    @Test
    public void should_result_true_when_file_name_matches() {

        final String fileName = "v0001_my-awnsome-apache-kafka-topic-migration.yaml";

        assertTrue(Loader.FILE_NAME_PATTERN.matcher(fileName).matches());
    }


    @Test
    public void should_result_true_when_extension_is_yml() {

        final String fileName = "v0001_my-awnsome-apache-kafka-topic-migration.yml";

        assertTrue(Loader.FILE_NAME_PATTERN.matcher(fileName).matches());
    }

    @Test
    public void should_result_true_when_file_has_maximum_chars() {

        final String fileName = "v0001_my-really-big-long-large-exaustive-apache-kafka-topic-migration-using-kottla-qqqqqqwwwwwweeeeeeeerrrrrrrrrrttttttttttttttttttttyyyyyyyyyyyyyyy-aaaaaaaaaaaaaaaaaaaaapppppppppppppppppppppaaaaaaaaaaaaaaaaaaaacccccccccccccccccccccccchhhhhhhhhhh0000.yaml";

        assertTrue(Loader.FILE_NAME_PATTERN.matcher(fileName).matches());
    }

    @Test
    public void should_throw_when_migration_file_not_found() {

        final String fileName = "./src/test/resources/topics/file-not-exists.yaml";

        assertThrows(FileNotFoundException.class, () ->
            Loader.load(Path.of(fileName)));
    }

    @Test
    public void should_throw_when_migration_is_an_invalid_yaml() {

        final String fileName = "./src/test/resources/topics/invalid.yaml";

        assertThrows(YAMLException.class, () ->
            Loader.load(Path.of(fileName)));
    }

    @Test
    public void should_load_create_migration() throws Exception {

        final String fileName = "./src/test/resources/topics/create.yaml";

        Model actual =
            Loader.load(Path.of(fileName));

        assertEquals("create", actual.getOperation());
        assertEquals("This is a note", actual.getNotes());
        assertEquals(1, actual.getPartitions());
        assertEquals(1, actual.getReplicationFactor());

        assertFalse(actual.getConfig().isEmpty());
        assertEquals(2, actual.getConfig().size());
    }
}
