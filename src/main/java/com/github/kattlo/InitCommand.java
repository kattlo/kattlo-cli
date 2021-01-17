package com.github.kattlo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import com.github.kattlo.util.VersionUtil;

import org.apache.kafka.clients.admin.AdminClientConfig;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "init",
    description = "To initialize new Kattlo project",
    showDefaultValues = true,
    mixinStandardHelpOptions = true
)
@Slf4j
public class InitCommand implements Runnable {

    private static final String COMMENTS = "Gererated by Kattlo v"
        + VersionUtil.appVersion();

    static final String KAFKA_FILE = "kafka.properties";
    static final String KATTLO_FILE = ".kattlo.yaml";

    @ParentCommand
    EntryCommand parent;

    @Spec
    CommandSpec spec;

    private File directory;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        defaultValue = ".",
        required = true
    )
    public void setDirectory(File directory) {
        this.directory = directory;
    }
    public File getDirectory() {
        return directory;
    }

    void validateOptions() {
        var directory = getDirectory();

        if(!directory.canWrite()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to write");
        }
    }

    @Override
    public void run() {
        validateOptions();

        // Create the kafka.properties within directory
        var kafkaProperties = new Properties();

        var servers = "configure-me:9092";
        if(Objects.nonNull(parent.getBootstrapServers())){
            servers = parent.getBootstrapServers();
        }
        kafkaProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
            servers);

        kafkaProperties.put(AdminClientConfig.CLIENT_ID_CONFIG, "kattlo-cli");

        var kafkaFile = new File(getDirectory(), KAFKA_FILE);
        try(var out = new FileOutputStream(kafkaFile)){
            kafkaProperties.store(out, COMMENTS);
        }catch(IOException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }
        log.debug("{} written to {}", KAFKA_FILE, kafkaFile);

        var kattloFile = new File(getDirectory(), KATTLO_FILE);
        try{
            kattloFile.createNewFile();
        }catch(IOException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }
        log.debug("{} written to {}", KATTLO_FILE, kattloFile);
    }

}
