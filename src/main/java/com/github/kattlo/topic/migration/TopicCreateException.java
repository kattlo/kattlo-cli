package com.github.kattlo.topic.migration;

/**
 * @author fabiojose
 */
public class TopicCreateException extends RuntimeException {
    private static final long serialVersionUID = 5224421179483658498L;

    TopicCreateException(){

    }

    TopicCreateException(String message){
        super(message);
    }

    TopicCreateException(String message, Throwable cause){
        super(message, cause);
    }

}
