package com.github.kattlo.topic;

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
    description = "To show the topic current state or the history of migrations",
    showDefaultValues = true
)
@Slf4j
public class TopicInfoCommand implements Runnable {

    private ReportFormat format;
    private boolean history;
    private String topic;

    @ParentCommand
    TopicCommand parent;

    @Spec
    CommandSpec spec;

    @Inject
    Backend backend;

    @Inject
    Kafka kafka;

    private Reporter reporter = new PrintStreamReporter(System.out);

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
        paramLabel = "Topic name"
    )
    public void setTopic(String topic){
        this.topic = topic;
    }

    private void validateOptions() {
        parent.validateOptions();
    }

    @Override
    public void run() {
        validateOptions();

        log.debug("Showing the info of topic {}", topic);

        try(var admin = kafka.adminFor(parent.getParent()
                .getKafkaConfiguration())){

            backend.init(parent.getParent().getKafkaConfiguration());

            if(!history){
                var current = backend.current(ResourceType.TOPIC, topic);

                if(current.isPresent()){
                    reporter.current(current.get(), format);
                } else  {
                    throw new CommandLine.ExecutionException(spec.commandLine(),
                        "Topic not managed by Kattlo");
                }
            } else {
                var history = backend.history(ResourceType.TOPIC, topic);

                reporter.history(history, format);
            }
        }

    }
}
