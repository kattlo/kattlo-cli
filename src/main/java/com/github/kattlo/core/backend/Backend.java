package com.github.kattlo.core.backend;

import java.util.Optional;

/**
 * @author fabiojose
 */
public interface Backend {

    Optional<Migration> latest(ResourceType type, String name);

    Migration commit(MigrationToApply migration);

}
