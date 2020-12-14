package com.github.kattlo.core.backend.kafka;

/**
 * @author fabiojose
 */
public class KafkaBackendException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public KafkaBackendException(String message){
        super(message);
    }

}
