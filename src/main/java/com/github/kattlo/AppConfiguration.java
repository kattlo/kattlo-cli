package com.github.kattlo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.kafka.KafkaBackend;
import com.github.kattlo.core.kafka.Kafka;

/**
 * @author fabiojose
 */
@ApplicationScoped
public class AppConfiguration {

    @Produces
    @ApplicationScoped
    Backend backend() {
        return new KafkaBackend();
    }

    @Produces
    @ApplicationScoped
    Kafka kafka(){
        return new Kafka();
    }
}
