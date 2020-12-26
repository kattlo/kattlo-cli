package com.github.kattlo.core.backend.memory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;

import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.kafka.ResourceCommit;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class InMemoryMigrationBackendTest {

    final InMemoryMigrationBackend backend = new InMemoryMigrationBackend();

    @Test
    public void should_throw_when_migration_arg_is_null() {

        assertThrows(NullPointerException.class, () -> backend.commit(null));
    }

    @Test
    public void should_commit_the_applied_migration() {

        var commit = new ResourceCommit();
        commit.setVersion("v0001");
        commit.setOperation(OperationType.CREATE);
        commit.setNotes("Some notes");
        commit.setResourceType(ResourceType.TOPIC);
        commit.setResourceName("topic-1");
        commit.setTimestamp(LocalDateTime.now());
        commit.setAttributes(Map.of());

        InMemoryMigrationBackend
            .MIGRATIONS.put("{TOPIC}applied", commit);

        var actual = backend.current(ResourceType.TOPIC, "applied");

        assertTrue(actual.isPresent());
    }

    @Test
    public void should_result_empty_when_no_migrations() {

        var actual = backend.current(ResourceType.TOPIC, "name");

        assertTrue(actual.isEmpty());
    }

}
