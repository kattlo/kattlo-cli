package com.github.kattlo.topic;

import java.nio.file.Path;
import java.util.List;

/**
 * @author fabiojose
 */
public class TopicRuleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private List<String> details = List.of();
    private final Path file;

    public TopicRuleException(List<String> details, Path file){
        this.details = List.copyOf(details);
        this.file = file;
    }

    public List<String> getDetails() {
        return details;
    }

    public Path getFile() {
        return file;
    }

}
