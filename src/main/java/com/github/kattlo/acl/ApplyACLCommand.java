package com.github.kattlo.acl;

import java.io.File;
import java.util.Objects;

import javax.inject.Inject;

import com.github.kattlo.SharedOptionValues;
import com.github.kattlo.core.kafka.Kafka;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "acl",
    description = "Apply ACL Migrations",
    showDefaultValues = true,
    mixinStandardHelpOptions = true
)
public class ApplyACLCommand implements Runnable {

    private File directory;

    @Spec
    CommandSpec spec;

    @Inject
    Kafka kafka;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Directory with ACL migrations",
        defaultValue = ".",
        required = true
    )
    public void setDirectory(File directory){
        this.directory = Objects.requireNonNull(directory);
    }
    public File getDirectory() {
        return directory;
    }

    private void validateOptions() {
        SharedOptionValues.validateOptions();

        if(!directory.canRead()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to read");
        }
    }

    @Override
    public void run() {
        validateOptions();

        try(final var admin = kafka.adminFor(
                SharedOptionValues.getKafkaConfiguration())) {


        }
    }

}
