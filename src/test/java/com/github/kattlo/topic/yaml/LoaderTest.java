package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}
