package com.github.kattlo.core.backend.kafka;

import com.github.kattlo.core.backend.Migration;

import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author fabiojose
 */
@RegisterForReflection
public class MigrationSerializer extends JsonbSerializer<Migration> {

}
