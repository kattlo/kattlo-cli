package com.github.kattlo.core.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class MigrationLoaderTest {

    @Test
    public void should_throws_when_file_name_does_not_match() {

        final String fileName = "v001_my-migration.yaml";

        assertThrows(IllegalArgumentException.class, () ->
            MigrationLoader.matches(Path.of(fileName)));
    }

    @Test
    public void should_not_throws_when_file_name_matches() {

        final String fileName = "v0001_my-awnsome-apache-kafka-topic-migration.yaml";

        MigrationLoader.matches(Path.of(fileName));
    }

    @Test
    public void should_not_throws_when_extension_is_yml() {

        final String fileName = "v0001_my-awnsome-apache-kafka-topic-migration.yml";

        MigrationLoader.matches(Path.of(fileName));
    }

    @Test
    public void should_not_throws_when_file_has_maximum_chars() {

        final String fileName = "v0001_my-really-big-long-large-exaustive-apache-kafka-topic-migration-using-kottla-qqqqqqwwwwwweeeeeeeerrrrrrrrrrttttttttttttttttttttyyyyyyyyyyyyyyy-aaaaaaaaaaaaaaaaaaaaapppppppppppppppppppppaaaaaaaaaaaaaaaaaaaacccccccccccccccccccccccchhhhhhhhhhh0000.yaml";

        MigrationLoader.matches(Path.of(fileName));
    }

    @Test
    public void should_result_true_when_file_version_is_greater() {

        final Path file = Path.of("v0003_my-migration.yml");

        boolean actual = MigrationLoader.greater(file, "v0002");

        assertTrue(actual);
    }

    @Test
    public void should_result_false_when_file_version_is_not_greater() {

        final Path file = Path.of("v0002_my-migration.yml");

        boolean actual = MigrationLoader.greater(file, "v0002");

        assertFalse(actual);
    }

    @Test
    public void should_list_all_yaml_directory() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");

        var actual =
            MigrationLoader.list(directory)
                .count();

        assertEquals(5, actual);

    }
}
