package com.github.kattlo.core.backend;

import java.util.HashMap;
import java.util.Map;
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

    @SuppressWarnings("unchecked")
    public Map<String, Object> join(Map<String, Object> left, Migration right) {

        var attributesLeft = new HashMap<String, Object>();
        attributesLeft.putAll(Optional
            .ofNullable(left.get(ATTRIBUTES_KEYWORD))
            .map(a -> (Map<String, Object>)a)
            .orElse(new HashMap<String, Object>()));

        var attributesRight = new HashMap<String, Object>();
        attributesRight.putAll(right.getAttributes());

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
        log.debug("Left config: {}", leftConfig);

        var rightConfig = Optional.ofNullable(attributesRight.get(CONFIG_KEYWORD))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Right config: {}", rightConfig);

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
