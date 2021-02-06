package com.github.kattlo.topic;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.configuration.ConfigurationLoader;
import com.github.kattlo.core.configuration.condition.Condition;
import com.github.kattlo.core.exception.LoadException;
import com.github.kattlo.topic.yaml.TopicOperation;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class TopicRuleEnforcement {

    private static final String PARTITIONS_MESSAGE = "partitions: expected '%s', but was '%s'";
    private static final String REPLICATION_FACTOR_MESSAGE = "replicationFactor: expected '%s', but was '%s'";
    private static final String NAME_PATTERN_MESSAGE = "Expected the topic name to match '%s', but was '%s'";
    private static final String CONFIG_MESSAGE = "%s: expected '%s', but was '%s'";

    private TopicRules rules;

    private TopicRules getRules(File configuration) throws IOException {
        if(null== rules){
            var rulesMap = ConfigurationLoader.load(configuration, ResourceType.TOPIC);
            log.debug("Rules loaded {}", rulesMap);

            rules = TopicRules.of(rulesMap.orElse(Map.of()));
        }

        return rules;
    }

    private void check(Object value, Condition condition, String message, List<String> failures) {

        ofNullable(value)
            .filter(v -> !condition.execute(v))
            .map(v -> String.format(message,
                condition, v))
            .or(() -> Optional.empty())
            .ifPresent(failures::add);

    }

    /**
     * @throws TopicRuleException When the check does not pass
     */
    public void check(TopicOperation operation, File configuration) {

        try{
            var rules = getRules(configuration);
            log.debug("Migration to check {} against rules {}", operation, rules);

            var failures = new ArrayList<String>();

            check(operation.getTopic(), rules.getNamePattern(),
                NAME_PATTERN_MESSAGE, failures);

            check(operation.getPartitions(), rules.getPartitions(),
                PARTITIONS_MESSAGE, failures);

            check(operation.getReplicationFactor(), rules.getReplicationFactor(),
                REPLICATION_FACTOR_MESSAGE, failures);

            failures.addAll(
              operation.getConfig().entrySet().stream()
                .filter(c -> rules.getConfig().containsKey(c.getKey()))
                .filter(c -> !rules.getConfig().get(c.getKey()).execute(c.getValue()))
                .map(c -> String.format(CONFIG_MESSAGE, c.getKey(),
                                    rules.getConfig().get(c.getKey()), c.getValue()))
                .collect(Collectors.toList())
            );

            if(!failures.isEmpty()){
                throw new TopicRuleException(failures);
            }

        }catch(IOException e) {
            throw new LoadException(e.getMessage(), e);
        }

    }
}
