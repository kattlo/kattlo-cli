package com.github.kattlo;

import io.quarkus.test.junit.QuarkusTest;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.Test;

/**
 * @author fabiojose
 */
@QuarkusTest
public class EntryCommandTest {

    EntryCommand entry = new EntryCommand();

    @Test
    public void should_exit_code_2_when_config_file_does_not_exists(){

        String[] args = {
            "--config-file=./src/test/resources/_not_found.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "topic",
            "--directory=."
        };

        int actual =
            new CommandLine(entry).execute(args);

        assertEquals(2, actual);
    }

    @Test
    public void should_exit_code_2_when_kafka_cfg_files_does_not_exists() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/__not_found_kafka.properties",
            "topic",
            "--directory=."
        };

        int actual =
            new CommandLine(entry).execute(args);

        assertEquals(2, actual);

    }

    @Test
    public void should_override_the_bootstrap_servers() {

        var expected = "configure-me:9092";
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "--bootstrap-servers=" + expected,
            "topic",
            "--directory=."
        };

        var command = new CommandLine(entry);

        command.execute(args);

        //assert
        EntryCommand actualCommand = command.getCommand();
        var actualProperties = actualCommand.getKafkaConfiguration();
        var actual = actualProperties.getProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG);

        assertEquals(expected, actual);
    }
}
