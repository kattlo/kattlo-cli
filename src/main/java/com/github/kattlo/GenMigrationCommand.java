package com.github.kattlo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.exception.WriteException;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.Reporter;
import com.github.kattlo.topic.yaml.Loader;

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
    name = "migration",
    description = "To generate Kattlo migrations files",
    showDefaultValues = true,
    mixinStandardHelpOptions = true
)
@Slf4j
public class GenMigrationCommand implements Runnable {

    private static final String DEFAULT_VERSION = "0001";
    private static final String FILE_NAME = "v%s_rename-me.yaml";

    private static final String TOPIC_TEMPLATE = "/topic-migration.yaml";

    private ResourceType resource;
    private File directory;

    @ParentCommand
    GenCommand parent;

    @Spec
    CommandSpec spec;

    private Reporter reporter = new PrintStreamReporter(System.out);

    @Option(
        names = {
            "-r",
            "--resource"
        },
        description = "Resource type for the migration",
        paramLabel = "resource type",
        required = true
    )
    public void setResource(ResourceType resource) {
        this.resource = resource;
    }
    public ResourceType getResource(){
        return resource;
    }

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "To output the generated migration (default: the current)",
        paramLabel = "directory",
        required = true,
        defaultValue = "."
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

    String nextVersion() throws IOException {

        return Loader.list(directory)
            .sorted((m1, m2) -> m2.compareTo(m1))
            .findFirst()
            .map(Loader::versionOf)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Loader::versionNumberOf)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Integer::parseInt)
            .map(v -> v + 1)
            .map(v -> String.format("%04d", v))
            .orElse(DEFAULT_VERSION);

    }

    private void write(String value, FileWriter out) {
        try{
            out.write(value);
            out.write(System.lineSeparator());
        }catch(IOException e){
            throw new WriteException(e.getMessage(), e);
        }
    }

    private void newTopicMigration() throws IOException {

        var fileName = new File(getDirectory(),
            String.format(FILE_NAME, nextVersion()));

        log.debug("File name to write out the topic migration content {}",
            fileName);

        try(var in = getClass().getResourceAsStream(TOPIC_TEMPLATE);
            var out = new FileWriter(fileName)){

            new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .forEach(l -> write(l, out));

            reporter.generated(Path.of(fileName.toURI()));
        }
    }

    @Override
    public void run() {
        validateOptions();

        try{
            if(ResourceType.TOPIC.equals(getResource())){
                log.info("Will generate a TOPIC migration file");
                newTopicMigration();

            } else {
                throw new CommandLine.
                    ParameterException(spec.commandLine(),
                        "Unknow resource " + getResource());
            }
        }catch(IOException | WriteException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }
    }
}
