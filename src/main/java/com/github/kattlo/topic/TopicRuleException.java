package com.github.kattlo.topic;

import java.util.List;

/**
 * @author fabiojose
 */
public class TopicRuleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private List<String> details = List.of();

    public TopicRuleException(List<String> details){
        this.details = List.copyOf(details);
    }

    public TopicRuleException(String message) {
        super(message);
    }

    public List<String> getDetails() {
        return details;
    }

}
