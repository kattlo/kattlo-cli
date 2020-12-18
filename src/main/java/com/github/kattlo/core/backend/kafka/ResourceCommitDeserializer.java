package com.github.kattlo.core.backend.kafka;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author fabiojose
 */
@RegisterForReflection
public class ResourceCommitDeserializer
    extends JsonbDeserializer<ResourceCommit> {

    public ResourceCommitDeserializer(){
        this(ResourceCommit.class);
    }

    public ResourceCommitDeserializer(Class<ResourceCommit> type) {
        super(type);
    }

}
