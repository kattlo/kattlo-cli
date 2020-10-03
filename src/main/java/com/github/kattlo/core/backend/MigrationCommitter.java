package com.github.kattlo.core.backend;

/**
 * @author fabiojose
 */
public interface MigrationCommitter {

    Migration commit(MigrationToApply migration);

}
