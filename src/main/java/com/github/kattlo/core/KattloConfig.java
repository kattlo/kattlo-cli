package com.github.kattlo.core;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;

/**
 * @author fabiojose
 */
public class KattloConfig {

    Partitioner p = new DefaultPartitioner();

    public static final String TOPICS_MIGRATION_TOPIC_NAME =
        "__kattlo_topics_migrations";


}
