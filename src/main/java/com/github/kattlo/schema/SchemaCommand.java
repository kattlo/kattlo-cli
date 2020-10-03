package com.github.kattlo.schema;

import java.util.Objects;

import com.github.kattlo.EntryCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * @author fabiojose
 */
@Command(
    name = "schema",
    description = "Migrations for Schemas"
)
public class SchemaCommand implements Runnable {

    @ParentCommand
    private EntryCommand parent;

    private String diretory;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Diretory with schema migrations",
        required = true
    )
    public void setDirectory(String directory){
        this.diretory = Objects.requireNonNull(directory);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
    
}
