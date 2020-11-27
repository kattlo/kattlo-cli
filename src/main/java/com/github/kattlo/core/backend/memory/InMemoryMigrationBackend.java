package com.github.kattlo.core.backend.memory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.MigrationToApply;
import com.github.kattlo.core.backend.ResourceType;

/**
 * @author fabiojose
 */
public class InMemoryMigrationBackend implements
        Backend {

    static final Map<String, Migration> MIGRATIONS =
            new HashMap<>();

    static final String keyOf(ResourceType type, String name) {
        return "{" + type + "}" + name;
    }
    static final String keyOf(MigrationToApply applied) {
        return keyOf(applied.getResourceType(), applied.getResourceName());
    }

    @Override
    public Migration commit(MigrationToApply applied) {

        final Migration migration = new Migration();
        migration.setApplied(applied);
        migration.setTimestamp(LocalDateTime.now());
        migration.setStatus(ResourceStatus.AVAILABLE);

        MIGRATIONS.put(keyOf(applied), migration);

        return migration;
    }

    @Override
    public Optional<Migration> latest(ResourceType type, String name) {
        return Optional.ofNullable(MIGRATIONS.get(keyOf(type, name)));
    }

}
