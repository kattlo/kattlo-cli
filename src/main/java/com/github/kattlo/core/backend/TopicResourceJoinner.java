package com.github.kattlo.core.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class TopicResourceJoinner {

    public static final String ATTRIBUTES_KEYWORD = "attributes";
    public static final String CONFIG_KEYWORD = "config";
    public static final String ORIGINAL_KEYWORD = "original";

    public static void removeNullValues(Map<String, Object> m) {
        m.values().removeIf(Objects::isNull);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> join(Map<String, Object> left, Migration right) {

        var attributesLeft = new HashMap<String, Object>();
        attributesLeft.putAll(Optional
            .ofNullable(left.get(ATTRIBUTES_KEYWORD))
            .map(a -> (Map<String, Object>)a)
            .orElse(new HashMap<String, Object>()));
        log.debug("Left attributes before remove null values {}", attributesLeft);
        attributesLeft.values().removeIf(Objects::isNull);
        log.debug("Left attributes after remove null values {}", attributesLeft);

        var attributesRight = new HashMap<String, Object>();
        attributesRight.putAll(right.getAttributes());
        log.debug("Right attributes before remove null values {}", attributesRight);
        attributesRight.values().removeIf(Objects::isNull);
        log.debug("Right attributes after remove null values {}", attributesRight);

        var leftCopy = new HashMap<String, Object>();
        leftCopy.putAll(left);
        leftCopy.put(ATTRIBUTES_KEYWORD, attributesLeft);

        var rightCopy = new HashMap<String, Object>();
        rightCopy.putAll(right.asMigrationMap());
        rightCopy.remove(ATTRIBUTES_KEYWORD);
        rightCopy.remove(ORIGINAL_KEYWORD);

        //config
        var leftConfig = Optional.ofNullable(attributesLeft.get(CONFIG_KEYWORD))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Left config before remove null values {}", leftConfig);
        leftConfig.values().removeIf(Objects::isNull);
        log.debug("Left config after remove null values {}", leftConfig);

        var rightConfig = Optional.ofNullable(attributesRight.get(CONFIG_KEYWORD))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Right config before remove null values {}", rightConfig);
        rightConfig.values().removeIf(Objects::isNull);
        log.debug("Right config after remove null values {}", rightConfig);

        var newConfig = new HashMap<String, Object>();
        newConfig.putAll(leftConfig);
        newConfig.putAll(rightConfig);
        log.debug("Joined config: {}", newConfig);

        var newAttributes = new HashMap<>();
        newAttributes.putAll(attributesLeft);
        newAttributes.putAll(attributesRight);
        newAttributes.put(CONFIG_KEYWORD, newConfig);
        log.debug("Joined attributes: {}", newAttributes);

        var joined = new HashMap<String, Object>();
        joined.putAll(leftCopy);
        joined.putAll(rightCopy);
        joined.put(ATTRIBUTES_KEYWORD, newAttributes);

        log.debug("Joined migration: {}", joined);

        return joined;
    }
}
