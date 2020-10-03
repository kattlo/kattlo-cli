package com.github.kattlo.core.backend.memory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.ResourceType;

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

        InMemoryMigrationBackend
            .MIGRATIONS.put("{TOPIC}applied", new Migration());

        var actual = backend.latest(ResourceType.TOPIC, "applied");

        assertTrue(actual.isPresent());
    }

    @Test
    public void should_result_empty_when_no_migrations() {

        var actual = backend.latest(ResourceType.TOPIC, "name");

        assertTrue(actual.isEmpty());
    }

}
