package com.github.kattlo.core.backend;

import java.util.Optional;

/**
 * @author fabiojose
 */
public interface Backend {

    /**
     * Fetches the current state of resource
     */
    //Optional<CurrentState> current(ResourceType type, String name);

    /**
     * Fetches the latest applied migration
     */
    Optional<Migration> latest(ResourceType type, String name);

    /**
     * Confirms an applied migration
     */
    Migration commit(MigrationToApply migration);

}
