package com.github.kattlo.core.backend.kafka;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

/**
 * @author fabiojose
 */
public class ResourceCommitDeserializer
    extends JsonbDeserializer<ResourceCommit> {

    public ResourceCommitDeserializer(){
        this(ResourceCommit.class);
    }

    public ResourceCommitDeserializer(Class<ResourceCommit> type) {
        super(type);
    }

}
