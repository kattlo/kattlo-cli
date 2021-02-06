package com.github.kattlo.topic;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Path;

import com.github.kattlo.core.exception.LoadException;
import com.github.kattlo.topic.yaml.Loader;
import com.github.kattlo.topic.yaml.TopicOperationMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TopicRuleEnforcementTest {

    private final TopicRuleEnforcement enforcement = new TopicRuleEnforcement();
    private final TopicOperationMapper mapper =
        Mappers.getMapper(TopicOperationMapper.class);

    @Test
    public void should_throw_load_exception_when_cant_load_the_configuration() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_not_found.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_partitions-1.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        assertThrows(LoadException.class, () ->
            enforcement.check(migration, configuration));

    }

    @Test
    public void should_throw_when_fail_the_check_of_partitions() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_partitions-1.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        var actual =
            assertThrows(TopicRuleException.class, () ->
                enforcement.check(migration, configuration));

        assertFalse(actual.getDetails().isEmpty());
        assertThat(actual.getDetails(), Matchers.hasItem("partitions: expected '>=3', but was '1'"));

    }

    @Test
    public void should_throw_when_fail_the_check_of_replication_factor() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_replication-factor-1.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        var actual =
            assertThrows(TopicRuleException.class, () ->
                enforcement.check(migration, configuration));

        assertFalse(actual.getDetails().isEmpty());
        assertThat(actual.getDetails(), Matchers.hasItem("replicationFactor: expected '==2', but was '1'"));
    }

    @Test
    public void should_throw_when_fail_the_check_of_name_pattern() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_name-pattern-upper-case.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        var actual =
            assertThrows(TopicRuleException.class, () ->
                enforcement.check(migration, configuration));

        assertFalse(actual.getDetails().isEmpty());
        assertThat(actual.getDetails(), Matchers.hasItem("Expected the topic name to match '^[a-z0-9\\-]{1,255}$', but was 'Topic-Name'"));

    }

    @Test
    public void should_pass_when_no_rules_at_all() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_empty.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_name-pattern-upper-case.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        enforcement.check(migration, configuration);

    }

    @Test
    public void should_throw_with_details_when_fail_the_check_of_config() {

        var configuration = new File("./src/test/resources/topics/rules/.kattlo_topic.yaml");
        var migrationFile = Path.of("./src/test/resources/topics/rules/migration/v0000_config.yaml");

        var migration = mapper.map(Loader.load(migrationFile), migrationFile);

        var actual =
            assertThrows(TopicRuleException.class, () ->
                enforcement.check(migration, configuration));

        assertFalse(actual.getDetails().isEmpty());
        assertThat(actual.getDetails(), Matchers.hasItem("compression.type: expected 'in [lz4, snappy]', but was 'gzip'"));
        assertThat(actual.getDetails(), Matchers.hasItem("retention.ms: expected '!=-1', but was '-1'"));
        assertThat(actual.getDetails(), Matchers.hasItem("min.cleanable.dirty.ratio: expected '>=0.00001', but was '0.000001'"));

    }

    @Test
    public void should_fail_when_value_must_be_a_number_and_its_a_string() {

    }

    @Test
    public void should_fail_when_value_must_be_a_number_and_its_a_boolean() {

    }
}
