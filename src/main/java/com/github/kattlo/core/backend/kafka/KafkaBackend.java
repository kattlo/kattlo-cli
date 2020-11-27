package com.github.kattlo.core.backend.kafka;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.kattlo.core.backend.Backend2;
import com.github.kattlo.core.backend.Migration2;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;

/**
 * @author fabiojose
 */
public class KafkaBackend implements Backend2 {

    @Override
    public Resource commit(Migration2 applied) {
        return null;
    }

    @Override
    public Optional<Resource> current(ResourceType type, String name) {
        return null;
    }

    @Override
    public Stream<Migration2> history(ResourceType type, String name) {
        return null;
    }

}
