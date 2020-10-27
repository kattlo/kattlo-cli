package com.github.kattlo.topic.migration;

public class TopicPatchException extends RuntimeException {
    private static final long serialVersionUID = 2599773048864383137L;

    public TopicPatchException(String message) {
        super(message);
    }

    public TopicPatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
