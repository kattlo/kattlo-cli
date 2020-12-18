package com.github.kattlo.core.backend.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.kafka.ResourceCommit;

/**
 * @author fabiojose
 */
public class InMemoryMigrationBackend implements Backend {

    static final Map<String, ResourceCommit> MIGRATIONS = new HashMap<>();

    static final String keyOf(ResourceType type, String name) {
        return "{" + type + "}" + name;
    }

    static final String keyOf(Migration applied) {
        return keyOf(applied.getResourceType(), applied.getResourceName());
    }

    @Override
    public Resource commit(Migration applied) {

        var commit = ResourceCommit.from(applied);
        MIGRATIONS.put(keyOf(applied), commit);

        return Resource.from(commit);
    }

    @Override
    public Optional<Resource> current(ResourceType type, String name) {
        return Optional.ofNullable(MIGRATIONS.get(keyOf(type, name))).map(Resource::from);
    }

    @Override
    public Stream<Migration> history(ResourceType type, String name) {
        return Stream.empty();
    }

    @Override
    public void init(Properties properties) {
    }

}
