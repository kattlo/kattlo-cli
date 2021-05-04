package com.github.kattlo;

import javax.inject.Inject;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.ReportFormat;
import com.github.kattlo.core.report.Reporter;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "info",
    description = "To show the resource state or the history of migrations",
    showDefaultValues = true,
    mixinStandardHelpOptions = true
)
@Slf4j
public class InfoCommand implements Runnable {

    private ResourceType resource;
    private ReportFormat format;
    private boolean history;
    private String name;

    @ParentCommand
    EntryCommand parent;

    @Spec
    CommandSpec spec;

    @Inject
    Backend backend;

    @Inject
    Kafka kafka;

    @Mixin
    Shared shared;

    private Reporter reporter = new PrintStreamReporter(System.out);

    @Option(
        names = {
            "--resource"
        },
        paramLabel = "The resource type",
        required = true
    )
    private void setResource(ResourceType resource){
        this.resource = resource;
    }

    @Option(
        names = {
            "--format"
        },
        paramLabel = "The format to print in the console",
        defaultValue = "PLAIN"
    )
    public void setFormat(ReportFormat format){
        this.format = format;
    }

    @Option(
        names = {
            "--history"
        },
        paramLabel = "Show migration history",
        defaultValue = "false"
    )
    public void setHistory(boolean history){
        this.history = history;
    }

    @Parameters(
        index = "0",
        paramLabel = "resource name"
    )
    public void setName(String name){
        this.name = name;
    }

    private void validateOptions() {
        Shared.validateOptions();
    }

    @Override
    public void run() {
        validateOptions();

        log.debug("Showing the info of resource {} {}", resource, name);

        try(var admin = kafka.adminFor(Shared
                .getKafkaConfiguration())){

            backend.init(Shared.getKafkaConfiguration());

            if(!history){
                var current = backend.current(resource, name);

                if(current.isPresent()){
                    reporter.current(current.get(), format);
                } else  {
                    throw new CommandLine.ExecutionException(spec.commandLine(),
                        "Topic not managed by Kattlo");
                }
            } else {
                var history = backend.history(resource, name);

                reporter.history(history, format);
            }
        }

    }
}
