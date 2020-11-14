package com.github.kattlo.topic;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public final class TopicUtils {
    private TopicUtils(){}

    public static Optional<TopicDescription> describe(String topic, AdminClient admin)
            throws ExecutionException, InterruptedException {

        log.debug("Try to describe the {}", topic);
        var topicsResult = admin.describeTopics(List.of(topic));

        var descriptions = topicsResult.all().get();
        log.debug("Description of {}: {}", topic, descriptions);

        return Optional.ofNullable(descriptions.get(topic));
    }
}
