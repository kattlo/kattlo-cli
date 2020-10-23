package com.github.kattlo.topic.yaml;

/**
 * @author fabiojose
 */
public class LoadException extends RuntimeException {
    private static final long serialVersionUID = 8989901846495856854L;

    public LoadException() {
    }

    public LoadException(final Throwable cause) {
        super(cause);
    }

    public LoadException(final String message) {
        super(message);
    }
}
