package com.github.kattlo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.memory.InMemoryMigrationBackend;
import com.github.kattlo.core.kafka.Kafka;

/**
 * @author fabiojose
 */
@ApplicationScoped
public class AppConfiguration {

    @Produces
    @ApplicationScoped
    Backend backend() {
        return new InMemoryMigrationBackend();
    }

    @Produces
    @ApplicationScoped
    Kafka kafka(){
        return new Kafka();
    }
}
