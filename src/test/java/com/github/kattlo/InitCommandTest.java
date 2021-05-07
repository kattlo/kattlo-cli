package com.github.kattlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InitCommandTest {

    @Mock
    EntryCommand parent;

    @Mock
    CommandSpec spec;

    @InjectMocks
    InitCommand command;

    @Test
    public void should_throw_when_directory_does_not_exists() {

        // setup
        var directory = new File("./build/tmp/not_exists_dir");
        when(spec.commandLine())
            .thenReturn(new CommandLine(command));

        command.setDirectory(directory);

        assertThrows(CommandLine.ParameterException.class, () ->
            command.run());

    }

    @Test
    public void should_create_the_kafka_properties() {

        // setup
        var directory = new File("./build/tmp/init-0");
        directory.mkdirs();

        var expected = new File(directory, "kafka.properties");

        command.setDirectory(directory);

        // act
        command.run();

        // assert
        assertTrue(expected.exists());

    }

    @Test
    public void should_kafka_properties_contains_the_right_entries() throws IOException {

        // setup
        var expected = new Properties();
        expected.put("bootstrap.servers", "configure-me:9092");
        expected.put("client.id", "kattlo-cli");

        var directory = new File("./build/tmp/init-1");
        directory.mkdirs();

        var file = new File(directory, "kafka.properties");

        command.setDirectory(directory);

        // act
        command.run();

        // assert
        assertTrue(file.exists());

        var actual = new Properties();
        actual.load(new FileInputStream(file));

        assertEquals(expected, actual);
    }

    @Test
    public void should_create_the_kattlo_yaml() {

        // setup
        var directory = new File("./build/tmp/init-0");
        directory.mkdirs();

        var expected = new File(directory, ".kattlo.yaml");

        command.setDirectory(directory);

        // act
        command.run();

        // assert
        assertTrue(expected.exists());
    }

    @Test
    public void should_use_bootstrap_servers_if_available_as_option() throws IOException {

        // setup
        var expected = "my-kafka:9092";

        var directory = new File("./build/tmp/init-2");
        directory.mkdirs();

        var file = new File(directory, "kafka.properties");

        command.setDirectory(directory);

        try(var mocked = mockStatic(SharedOptionValues.class)){
            mocked.when(() -> SharedOptionValues.getBootstrapServers())
                .thenReturn(expected);

            // act
            command.run();

            // assert
            assertTrue(file.exists());

            var actual = new Properties();
            actual.load(new FileInputStream(file));

            assertEquals(expected,
                actual.getProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        }
    }
}
