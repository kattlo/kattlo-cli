package com.github.kattlo.topic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;

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
    name = "topic",
    description = "Migrations for Topics"
)
public class TopicCommand implements Runnable {

    @ParentCommand
    private EntryCommand parent;

    @Spec
    private CommandSpec spec;

    @Inject
    private Backend backend;

    private File directory;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Diretory with topic migrations",
        required = true
    )
    public void setDirectory(File directory){
        this.directory = Objects.requireNonNull(directory);
    }

    private void validate() {
        parent.validate();

        if(!directory.canRead()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to read");
        }
    }

    @Override
    public void run() {
        validate();

        // TODO fetch the 'topic' latest migration

        try {
            Files.list(Paths.get(directory.getAbsolutePath()))
                .forEach(migration -> {
                    System.out.println(migration);

                    //TODO Load yaml with migration

                    //TODO create, patch or remove?

                    //TODO apply the strategy
                });
        }catch(IOException e) {
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "failing to read migrations: " + e.getMessage());
        }
    }
}
