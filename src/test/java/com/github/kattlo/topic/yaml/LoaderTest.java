package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

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

        final String fileName = "./src/test/resources/topics/v0000_file-not-exists.yaml";

        assertThrows(LoadException.class, () ->
            Loader.load(Path.of(fileName)));
    }

    @Test
    public void should_throw_when_migration_is_an_invalid_yaml() {

        final String fileName = "./src/test/resources/topics/v0000_invalid.yaml";

        assertThrows(YAMLException.class, () ->
            Loader.load(Path.of(fileName)));
    }

    @Test
    public void should_load_create_migration() throws Exception {

        final String fileName = "./src/test/resources/topics/v0000_create0.yaml";

        Model actual =
            Loader.load(Path.of(fileName));

        assertEquals("create", actual.getOperation());
        assertEquals("This is a note", actual.getNotes());
        assertEquals(1, actual.getPartitions());
        assertEquals(1, actual.getReplicationFactor());

        assertFalse(actual.getConfig().isEmpty());
        assertEquals(2, actual.getConfig().size());
    }

    @Test
    public void should_load_topic_config_string() throws Exception {

        final String fileName = "./src/test/resources/topics/v0000_create1.yaml";

        Model actual =
            Loader.load(Path.of(fileName));


        assertThat(
            actual.getConfig().get("compression.type"),
            instanceOf(String.class));

        assertEquals("snappy", actual.getConfig().get("compression.type"));
    }

    @Test
    public void should_load_topic_config_long() throws Exception {

        final String fileName = "./src/test/resources/topics/v0000_create1.yaml";

        Model actual =
            Loader.load(Path.of(fileName));

        assertThat(actual.getConfig().get("retention.ms"),
            instanceOf(Long.class));

        assertEquals(24324324234242L, actual.getConfig().get("retention.ms"));
    }

    @Test
    public void should_load_topic_config_double() throws Exception {

        final String fileName = "./src/test/resources/topics/v0000_create1.yaml";

        Model actual =
            Loader.load(Path.of(fileName));

        assertThat(actual.getConfig().get("min.cleanable.dirty.ratio"),
            instanceOf(Double.class));

        assertEquals(0.2D, actual.getConfig().get("min.cleanable.dirty.ratio"));
    }

    @Test
    public void should_load_topic_config_boolean() throws Exception {

        final String fileName = "./src/test/resources/topics/v0000_create1.yaml";

        Model actual =
            Loader.load(Path.of(fileName));

        assertThat(actual.getConfig().get("preallocate"),
            instanceOf(Boolean.class));

        assertEquals(Boolean.TRUE, actual.getConfig().get("preallocate"));
    }

    @Test
    public void should_list_all_yaml_directory() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");

        var actual =
            Loader.list(directory)
                .count();

        assertEquals(5, actual);

    }

    @Test
    public void should_result_empty_when_thereis_no_next_version() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0001";
        final String topic = "undefined";

        Optional<TopicOperation> actual =
            Loader.next(currentVersion, topic, directory);

        assertTrue(actual.isEmpty());

    }

    @Test
    public void should_result_the_next_version() throws Exception {

        final String expected = "v0002";

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0001";
        final String topic = "payments";

        Optional<TopicOperation> actual =
            Loader.next(currentVersion, topic, directory);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual.get().getVersion());
    }

    @Test
    public void should_result_the_last_version() throws Exception {

        final String expected = "v0005";

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0004";
        final String topic = "payments";

        Optional<TopicOperation> actual =
            Loader.next(currentVersion, topic, directory);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual.get().getVersion());
    }

    @Test
    public void should_result_no_next_version() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0005";
        final String topic = "payments";

        Optional<TopicOperation> actual =
            Loader.next(currentVersion, topic, directory);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void should_result_the_first_version() throws Exception {

        final String expected = "v0001";

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0000";
        final String topic = "payments";

        Optional<TopicOperation> actual =
            Loader.next(currentVersion, topic, directory);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual.get().getVersion());
    }

    @Test
    public void should_result_true_when_file_version_is_greater() {

        final Path file = Path.of("v0003_my-migration.yml");

        boolean actual = Loader.greater(file, "v0002");

        assertTrue(actual);
    }

    @Test
    public void should_result_false_when_file_version_is_not_greater() {

        final Path file = Path.of("v0002_my-migration.yml");

        boolean actual = Loader.greater(file, "v0002");

        assertFalse(actual);
    }

    @Test
    public void should_load_all_newer_migrations_when_no_version() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0000";
        final String topic = "payments";

        final Stream<TopicOperation> actual =
            Loader.newer(currentVersion, topic, directory);

        assertEquals(5, actual.count());
    }

    @Test
    public void should_load_newer_migrations_from_v0003() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0003";
        final String topic = "payments";

        final Stream<TopicOperation> actual =
            Loader.newer(currentVersion, topic, directory);

        assertEquals(2, actual.count());
    }

    @Test
    public void should_load_empty_stream_when_there_is_no_newer() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String currentVersion = "v0005";
        final String topic = "payments";

        final Stream<TopicOperation> actual =
            Loader.newer(currentVersion, topic, directory);

        assertEquals(0, actual.count());
    }

    @Test
    public void should_load_all_migrations_for_a_topic() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String topic = "payments";

        final Stream<TopicOperation> actual =
            Loader.all(topic, directory);

        assertEquals(5, actual.count());
    }

    @Test
    public void should_load_nothing_when_no_migrations() throws Exception {

        final Path directory = Path.of("./src/test/resources/topics/many_migrations_0/");
        final String topic = "not-found-topic";

        final Stream<TopicOperation> actual =
            Loader.all(topic, directory);

        assertEquals(0, actual.count());
    }
}
