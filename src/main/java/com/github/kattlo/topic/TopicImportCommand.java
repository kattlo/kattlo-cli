package com.github.kattlo.topic;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.Reporter;
import com.github.kattlo.topic.yaml.Model;
import com.github.kattlo.topic.yaml.TopicOperation;
import com.github.kattlo.topic.yaml.Writer;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.protocol.types.Field.Bool;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@Command(
    name = "import",
    description = "To import existing topic"
)
@Slf4j
public class TopicImportCommand implements Runnable {

    private static final String INITIAL_VERSION = "v0001";
    static final String INITIAL_FILE_NAME = INITIAL_VERSION + "_create-topic.yaml";
    private static final String NOTES = "Automatically imported by Kattlo®";

    @ParentCommand
    TopicCommand parent;

    @Spec
    CommandSpec spec;

    @Inject
    Backend backend;

    @Inject
    Kafka kafka;

    private final Reporter reporter = new PrintStreamReporter(System.out);

    private String topicName;

    private void validateOptions() {
        parent.validateOptions();
        var directory = parent.getDirectory();

        if(!directory.canWrite()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to write");
        }
    }

    private TopicOperation from(TopicDescription description, Config configs, Path file){

        Map<String, Object> config = configs.entries().stream()
            .filter(c -> !c.isDefault())
            .collect(Collectors.toMap(ConfigEntry::name, ConfigEntry::value));

        log.debug("Kafka Topic configs as Map (without defaults) {}", config);

        var operation = TopicOperation.builder()
            .version(INITIAL_VERSION)
            .operation(OperationType.CREATE.name().toLowerCase())
            .notes(NOTES)
            .topic(description.name())
            .partitions(description.partitions().size())
            .replicationFactor(description.partitions().iterator().next().replicas().size())
            .config(config)
            .file(file)
            .build();

        log.debug("TopicOperation to created from existing topic {}", operation);

        return operation;
    }

    private Model toModel(TopicOperation operation) {

        var model = new Model();
        model.setOperation(operation.getOperation());
        model.setNotes(operation.getNotes());
        model.setTopic(operation.getTopic());
        model.setPartitions(operation.getPartitions());
        model.setReplicationFactor(operation.getReplicationFactor());
        model.setConfig(Map.copyOf(operation.getConfig()));

        return model;

    }

    @Option(
        names = {
            "-t",
            "--topic"
        },
        description = "Topic name",
        required = true
    )
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void run() {
        validateOptions();

        try(var admin = kafka.adminFor(parent.getParent()
                .getKafkaConfiguration())){

            backend.init(parent.getParent().getKafkaConfiguration());

            // describe topic
            var description =
              TopicUtils.describe(topicName, admin)
                  .orElseThrow(() -> new CommandLine.ParameterException(
                        spec.commandLine(), "Topic not found"));

            var configs =
                TopicUtils.configsOf(topicName, admin)
                    .orElseThrow(() -> new CommandLine.ParameterException(
                        spec.commandLine(), "Topic not found"));

            // file v0001_create-topic.yaml
            var file = Path.of(parent.getDirectory().getPath(), INITIAL_FILE_NAME);

            // instance of TopicOperation CREATE
            var operation = from(description, configs, file);

            // TopicOperation to Model
            var model = toModel(operation);

            // write TopicOperation to disc
            Writer.write(model, file);

            // instance of Migration
            var migration = operation.toMigration();

            // commit the create operation using the backend
            var state = backend.commit(migration);
            log.debug("Existing topic imported as {}", state);

            // report the import
            reporter.report(migration, Boolean.TRUE);

        }catch(InterruptedException | ExecutionException e){
            throw new CommandLine.ExecutionException(spec.commandLine(),
                e.getMessage(), e);
        }
    }
}
