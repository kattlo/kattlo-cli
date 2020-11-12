package com.github.kattlo.topic.migration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.errors.InterruptException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@RequiredArgsConstructor(access = AccessLevel.MODULE)
@Slf4j
public class RemoveStrategy implements Strategy {

    @NonNull
    private final TopicOperation operation;

    private Optional<TopicDescription> describe(String topic, AdminClient admin)
            throws ExecutionException, InterruptedException {

        log.debug("Try to describe the {}", topic);
        var topicsResult = admin.describeTopics(List.of(topic));

        var descriptions = topicsResult.all().get();
        log.debug("Description of {}: {}", topic, descriptions);

        return Optional.ofNullable(descriptions.get(topic));
    }

    @Override
    public void execute(AdminClient admin) {

        try {
            var description = describe(operation.getTopic(), admin)
                .orElseThrow(() ->
                    new TopicRemoveException("topic does not exists: "
                        + operation.getTopic()));

            var result = admin.deleteTopics(List.of(description.name()));
            log.debug("Topic removed: {}", operation.getTopic());

            result.all().get();

        }catch(ExecutionException | InterruptedException e) {
            throw new TopicRemoveException(e.getMessage(), e);
        }
    }

}
