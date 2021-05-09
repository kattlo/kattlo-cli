package com.github.kattlo;

import com.github.kattlo.acl.ApplyACLCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * @author fabiojose
 */
@Command(
    name = "apply",
    aliases = {"a"},
    mixinStandardHelpOptions = true,
    subcommands = {
        ApplyACLCommand.class
    }
)
public class ApplyCommand {

    @ParentCommand
    EntryCommand parent;

}
