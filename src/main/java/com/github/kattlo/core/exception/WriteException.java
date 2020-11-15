package com.github.kattlo.core.exception;

/**
 * @author fabiojose
 */
public class WriteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WriteException(String message){
        super(message);
    }

    public WriteException(String message, Throwable cause){
        super(message, cause);
    }

}
