package com.github.kattlo.topic;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.kattlo.core.configuration.condition.Condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class TopicRules {

    private static final String NAME_PATTERN_ATT = "namePattern";
    private static final String PARTITIONS_ATT = "partitions";
    private static final String REPLICATION_FACTOR_ATT = "replicationFactor";
    private static final String CONFIG_ATT = "config";

    private final Condition namePattern;
    private final Condition partitions;
    private final Condition replicationFactor;
    private final Map<String, Condition> config;

    @SuppressWarnings("unchecked")
    private static Condition condition(Map<String, Object> rules, String rule){
        log.debug("rules map {}, rule to get {}", rules, rule);

        var result = ofNullable(rules.get(rule))
            .filter(Objects::nonNull)
            .map(p -> (Map<String, Object>)p)
            .map(c -> c.entrySet())
            .filter(c -> !c.isEmpty())
            .map(c -> c.iterator().next())
            .map(c -> Condition.of(c.getKey(), c.getValue()))
            .orElse(Condition.byPass());

        log.debug("Condition created {}", result);

        return result;
    }

    @SuppressWarnings("unchecked")
    public static TopicRules of(Map<String, Object> rules) {
        Objects.requireNonNull(rules, "provide a non-null rules arg");

        var namePattern = ofNullable(rules.get(NAME_PATTERN_ATT))
            .filter(Objects::nonNull)
            .map(operand -> Condition.of("regex", operand))
            .orElse(Condition.byPass());

        var partitions = condition(rules, PARTITIONS_ATT);
        var replicationFactor = condition(rules, REPLICATION_FACTOR_ATT);

        var configs = ofNullable(rules.get(CONFIG_ATT))
            .filter(Objects::nonNull)
            .map(c -> (Map<String, Object>)c)
            .map(c -> c.entrySet());

        Map<String, Condition> config = new HashMap<>();
        for(var configEntry : configs.orElse(Set.of())) {
            config.put(configEntry.getKey(),
                condition((Map<String, Object>)rules.get(CONFIG_ATT),
                    configEntry.getKey()));
        }

        return new TopicRules(namePattern, partitions, replicationFactor,
            Map.copyOf(config));
    }
}
