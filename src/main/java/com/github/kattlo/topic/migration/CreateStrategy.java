package com.github.kattlo.topic.migration;

import static java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@RequiredArgsConstructor
@Slf4j
public class CreateStrategy implements Strategy {

    @NonNull
    private final TopicOperation operation;

    @Override
    public void execute(AdminClient admin) {
        log.debug("AdminClient {}", admin);

        log.debug("TopicOperation to perform {}", operation);

        var newTopic = new NewTopic(
            operation.getTopic(),
            Optional.ofNullable(operation.getPartitions()),
            Optional.ofNullable(operation.getReplicationFactor())
                .map(i -> i.shortValue()));

        newTopic.configs(operation.getConfig().entrySet()
            .stream()
            .map(e -> new SimpleEntry<>(e.getKey(), e.getValue().toString()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

        log.debug("NewTopic {}", newTopic);

        var result = admin.createTopics(Collections.singletonList(newTopic));

        try {
            result.all().get();
            log.debug("Topic created: {}", operation.getTopic());

        }catch(InterruptedException | ExecutionException e){
            log.error(e.getMessage(), e);
            throw new TopicCreateException(e.getMessage(), e);
        }
    }

}
