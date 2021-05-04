package com.github.kattlo;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import com.github.kattlo.topic.TopicCommand;
import com.github.kattlo.util.VersionUtil;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@TopCommand
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
public class EntryCommand {

    private static final String DEFAULT_CONFIG_FILE = ".kattlo.yaml";

    private File configuration;

    @Spec
    private CommandSpec spec;

    @Mixin
    Shared shared;

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
}
