package com.github.kattlo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import com.github.kattlo.core.exception.WriteException;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.Reporter;
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

    private static final String KATTLO_FILE_TEMPLATE = "/kattlo.yaml";

    static final String KAFKA_FILE = "kafka.properties";
    static final String KATTLO_FILE = ".kattlo.yaml";

    @ParentCommand
    EntryCommand parent;

    @Spec
    CommandSpec spec;

    private File directory;

    private final Reporter reporter = new PrintStreamReporter(System.out);

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

    private void write(String value, FileWriter out) {
        try{
            out.write(value);
            out.write(System.lineSeparator());
        }catch(IOException e){
            throw new WriteException(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        validateOptions();

        // Create the kafka.properties within directory
        var kafkaProperties = new Properties();

        var servers = "configure-me:9092";
        if(Objects.nonNull(SharedOptionValues.getBootstrapServers())){
            servers = SharedOptionValues.getBootstrapServers();
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
        try(var in = getClass().getResourceAsStream(KATTLO_FILE_TEMPLATE);
            var out = new FileWriter(kattloFile)){

            new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .forEach(l -> write(l, out));

        }catch(IOException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }
        log.debug("{} written to {}", KATTLO_FILE, kattloFile);

        reporter.initialized(directory.toPath());
    }

}
