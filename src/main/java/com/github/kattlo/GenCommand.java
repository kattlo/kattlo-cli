package com.github.kattlo;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
    name = "gen",
    description = "To create Kattlo stuff and other convenience artifacts",
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    subcommands = {
        GenMigrationCommand.class
    }
)
public class GenCommand implements Runnable {

    @ParentCommand
    EntryCommand parent;

    @Override
    public void run() {

    }

}
