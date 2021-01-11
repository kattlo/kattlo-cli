package com.github.kattlo.topic;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@UtilityClass
@Slf4j
public final class TopicUtils {

    public static Optional<TopicDescription> describe(String topic, AdminClient admin)
            throws ExecutionException, InterruptedException {

        try {

            log.debug("Try to describe the {}", topic);
            var topicsResult = admin.describeTopics(List.of(topic));

            var descriptions = topicsResult.all().get();
            log.debug("Description of {}: {}", topic, descriptions);

            return Optional.ofNullable(descriptions.get(topic));

        }catch(ExecutionException e){
            if(e.getCause() instanceof UnknownTopicOrPartitionException){
                return Optional.empty();
            } else {
                throw e;
            }
        }

    }

    public static Optional<Config> configsOf(String topic, AdminClient admin)
            throws ExecutionException, InterruptedException {

        log.debug("Try to get configs of {}", topic);
        var resource = new ConfigResource(Type.TOPIC, topic);
        var result = admin.describeConfigs(List.of(resource));

        var config = result.all().get().get(resource);
        log.debug("Configs of {}: {}", topic, config);

        return Optional.ofNullable(config);
    }
}
