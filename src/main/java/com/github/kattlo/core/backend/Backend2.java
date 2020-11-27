package com.github.kattlo.core.backend;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author fabiojose
 */
public interface Backend2 {

    /**
     * Confirms an applied migration
     */
    Resource commit(Migration2 applied);

    /**
     * Fetches the current state of resource
     */
    Optional<Resource> current(ResourceType type, String name);

    /**
     * Fetches the resource migration history
     */
    Stream<Migration2> history(ResourceType type, String name);
}
