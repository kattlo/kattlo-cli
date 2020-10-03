package com.github.kattlo.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import picocli.CommandLine;

/**
 * @author fabiojose
 */
@QuarkusTest
public class TopicCommandTest {

    private TopicCommand topic = new TopicCommand();
   
    @Test
    public void should_exit_code_2_when_directory_not_exists() {

        String[] args = {
            "--config-file=./src/test/java/resources/.bakon.yaml",
            "--kafka-cfg=./src/test/java/resources/kafka.properties",
            "topic",
            "--directory=./_not_exists"
        };

        int actual = 
            new CommandLine(topic).execute(args);

        assertEquals(2, actual);
    }
}
