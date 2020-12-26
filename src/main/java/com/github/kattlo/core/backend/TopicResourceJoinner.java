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

    @SuppressWarnings("unchecked")
    public Map<String, Object> join(Map<String, Object> left, Migration right) {

        var attributesLeft = new HashMap<String, Object>();
        attributesLeft.putAll(Optional
            .ofNullable(left.get("attributes"))
            .map(a -> (Map<String, Object>)a)
            .orElse(new HashMap<String, Object>()));

        var attributesRight = new HashMap<String, Object>();
        attributesRight.putAll(right.getAttributes());

        var leftCopy = new HashMap<String, Object>();
        leftCopy.putAll(left);
        leftCopy.put("attributes", attributesLeft);

        var rightCopy = new HashMap<String, Object>();
        rightCopy.putAll(right.asMigrationMap());

        rightCopy.remove("attributes");
        rightCopy.remove("original");

        //config
        var leftConfig = Optional.ofNullable(attributesLeft.get("config"))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Left config: {}", leftConfig);

        var rightConfig = Optional.ofNullable(attributesRight.get("config"))
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
        newAttributes.put("config", newConfig);
        log.debug("Joined attributes: {}", newAttributes);

        var joined = new HashMap<String, Object>();
        joined.putAll(leftCopy);
        joined.putAll(rightCopy);
        joined.put("attributes", newAttributes);

        log.debug("Joined migration: {}", joined);

        return joined;
    }
}
