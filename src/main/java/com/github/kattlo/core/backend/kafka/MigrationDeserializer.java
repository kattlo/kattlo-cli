package com.github.kattlo.core.backend.kafka;

import com.github.kattlo.core.backend.Migration;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author fabiojose
 */
@RegisterForReflection
public class MigrationDeserializer extends JsonbDeserializer<Migration> {

    public MigrationDeserializer(){
        this(Migration.class);
    }

    public MigrationDeserializer(Class<Migration> type) {
        super(type);
    }

}
