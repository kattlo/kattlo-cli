package com.github.kattlo.core.backend.kafka;

import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author fabiojose
 */
@RegisterForReflection
public class ResourceCommitSerializer
    extends JsonbSerializer<ResourceCommit> {

}
