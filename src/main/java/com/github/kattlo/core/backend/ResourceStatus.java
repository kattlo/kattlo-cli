package com.github.kattlo.core.backend;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author fabiojose
 */
@RegisterForReflection
public enum ResourceStatus {

    AVAILABLE,
    DELETED;

}
