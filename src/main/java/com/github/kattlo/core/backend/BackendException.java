package com.github.kattlo.core.backend;

/**
 * @author fabiojose
 */
public class BackendException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BackendException(String message){
        super(message);
    }

    public BackendException(String message, Throwable cause) {
        super(message, cause);
    }

}
