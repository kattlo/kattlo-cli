package com.github.kattlo;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import com.github.kattlo.topic.TopicCommand;
import com.github.kattlo.util.VersionUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "kattlo",
    versionProvider = VersionUtil.QuarkusVersionProvider.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        ApplyCommand.class,
        TopicCommand.class,
        InfoCommand.class,
        GenCommand.class,
        InitCommand.class
    }
)
public class EntryCommand implements Runnable {

    private static final String DEFAULT_CONFIG_FILE = ".kattlo.yaml";

    private File configuration;

    @Spec
    private CommandSpec spec;

    @Option(
        names = {
            "--config-file"
        },
        description = "Kattlo configurations",
        required = false
    )
    public void setConfiguration(File configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    public File getConfiguration() {

        this.configuration = Optional.ofNullable(configuration)
            .filter(Objects::nonNull)
            .orElseGet(() -> new File(DEFAULT_CONFIG_FILE));

        if(this.configuration.exists()){
            return configuration;
        } else {
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                    configuration.getAbsolutePath() + " not found");
        }

    }

    @Option(
        names = {
            "--bootstrap-servers"
        },
        description = "host/port pairs to connect the Apache Kafka®",
        required = false
    )
    public void setBootstrapServers(String bootstrapServers) {
        SharedOptionValues.setBootstrapServers(bootstrapServers);
    }

    @Option(
        names = {
            "--kafka-config-file"
        },
        description = "Properties file for Apache Kafka® clients",
        required = true,
        defaultValue = "kafka.properties"
    )
    public void setKafkaConfiguration(File kafkaConfiguration) {
        SharedOptionValues.setKafkaConfiguration(kafkaConfiguration);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
