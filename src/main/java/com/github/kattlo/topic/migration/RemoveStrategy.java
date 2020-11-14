package com.github.kattlo.topic.migration;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.github.kattlo.topic.TopicUtils;
import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

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

    @Override
    public void execute(AdminClient admin) {

        try {
            var description = TopicUtils.describe(operation.getTopic(), admin)
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
