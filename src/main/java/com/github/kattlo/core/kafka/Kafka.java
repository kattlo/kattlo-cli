package com.github.kattlo.core.kafka;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;

/**
 * @author fabiojose
 */
public class Kafka {

    public AdminClient adminFor(Properties configs) {
        return AdminClient.create(configs);
    }

}
