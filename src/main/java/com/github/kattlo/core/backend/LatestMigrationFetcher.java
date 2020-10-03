package com.github.kattlo.core.backend;

import java.util.Optional;

/**
 * @author fabiojose
 */
public interface LatestMigrationFetcher {

    Optional<Migration> latest(ResourceType type, String name);

}
