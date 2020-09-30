package org.acme.getting.started;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import ottla.topic.TopicCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(
    mixinStandardHelpOptions = true,
    subcommands = {
        TopicCommand.class,
        SchemaCommand.class
    }
)
public class EntryCommand {

    public String hello() {
        return "hello";
    }
}