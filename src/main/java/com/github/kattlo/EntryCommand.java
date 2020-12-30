package com.github.kattlo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import com.github.kattlo.topic.TopicCommand;
import com.github.kattlo.util.VersionUtil;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
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
        TopicCommand.class,
    }
)
public class EntryCommand {

    private File configuration;
    private Properties configurationValues;

    private boolean verbose;

    private File kafkaConfiguration;
    private Properties kafkaConfigurationValues;

    @Spec
    private CommandSpec spec;

    @Option(
        names = {
            "--config-file"
        },
        description = "Configuration file YAML for migrations",
        required = true
    )
    public void setConfiguration(File configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    public Properties getConfiguration() {
        if(null== configurationValues){
            configurationValues = new Properties();
            try{
                configurationValues.load(new FileInputStream(configuration));
            }catch(IOException e){
                throw new CommandLine
                    .ParameterException(spec.commandLine(),
                        configuration.getAbsolutePath() + " can't be read");
            }
        }

        return configurationValues;
    }

    @Option(
        names = "--debug",
        description = "Verbose mode"
    )
    public void setVerbose(boolean[] verbose) {
        this.verbose = verbose.length > 0;

        //Logger log = Logger.getLogger("ROOT");
        //log.info("Testing . . .");
    }

    public boolean isVerbose(){
        return verbose;
    }

    @Option(
        names = {
            "--kafka-config-file"
        },
        description = "Properties file for Apache Kafka® Admin Client",
        required = true
    )
    public void setKafkaConfiguration(File kafkaConfiguration) {
        this.kafkaConfiguration = Objects.requireNonNull(kafkaConfiguration);
    }

    public Properties getKafkaConfiguration() {
        if(null== kafkaConfigurationValues){
            kafkaConfigurationValues = new Properties();

            try{
                kafkaConfigurationValues
                    .load(new FileReader(kafkaConfiguration));
            }catch(IOException e){
                throw new CommandLine
                    .ParameterException(spec.commandLine(),
                        kafkaConfiguration.getAbsolutePath() + " can't be read");
            }
        }
        return kafkaConfigurationValues;
    }

    public void validateOptions() {
        if(!configuration.exists()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        configuration.getAbsolutePath() + " not found");
        }

        if(!kafkaConfiguration.exists()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                    kafkaConfiguration.getAbsolutePath() + " not found");
        }
    }
}
