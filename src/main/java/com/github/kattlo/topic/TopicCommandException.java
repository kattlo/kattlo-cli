package com.github.kattlo.topic;

/**
 * @author fabiojose
 */
public class TopicCommandException extends RuntimeException {
    private static final long serialVersionUID = -2068361479841818915L;

    public TopicCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
