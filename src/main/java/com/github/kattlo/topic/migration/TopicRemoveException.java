package com.github.kattlo.topic.migration;

/**
 * @author fabiojose
 */
public class TopicRemoveException extends RuntimeException {
    private static final long serialVersionUID = -4110676408010781698L;

    public TopicRemoveException(String message) {
        super(message);
    }

    public TopicRemoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
