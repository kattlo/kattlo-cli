package com.github.kattlo.topic.migration;

/**
 * @author fabiojose
 */
public class TopicCreateException extends RuntimeException {
    private static final long serialVersionUID = 5224421179483658498L;

    public TopicCreateException(){

    }

    public TopicCreateException(String message){
        super(message);
    }

    public TopicCreateException(String message, Throwable cause){
        super(message, cause);
    }

}
