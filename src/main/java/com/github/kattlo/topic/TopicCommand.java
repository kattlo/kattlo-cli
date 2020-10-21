package com.github.kattlo.topic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.MigrationToApply;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.topic.migration.Strategy;
import com.github.kattlo.topic.yaml.Loader;
import com.github.kattlo.topic.yaml.TopicOperation;
import com.github.kattlo.topic.yaml.TopicOperationMapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class TopicCommand implements Runnable {

    private static final String NO_VERSION = "v0000";

    @ParentCommand
    EntryCommand parent;

    @Spec
    CommandSpec spec;

    @Inject
    Backend backend;

    @Inject
    TopicOperationMapper mapper;

    @Inject
    Kafka kafka;

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

    private void validateOptions() {
        parent.validateOptions();

        if(!directory.canRead()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to read");
        }
    }

    Strategy strategyOf(TopicOperation to){
        return Strategy.of(to);
    }

    @Override
    public void run() {
        validateOptions();

        try(final var admin = kafka.adminFor(parent.getKafkaConfiguration())) {

            final var migrationFiles =
                Loader.list(directory)
                    .collect(Collectors.toList());

            for(int i = 0; i < migrationFiles.size(); i++){
                var migrationFile = migrationFiles.get(i);

                //TODO Validate yaml against schema??
                final var migrationModel =
                    Loader.load(migrationFile);

                final var operation =
                    mapper.map(migrationModel, migrationFile);

                // fetch the 'topic' latest migration
                final var latestMigration =
                    backend.latest(
                        ResourceType.TOPIC,
                        operation.getTopic());

                final var currentVersion =
                    latestMigration
                        .map(Migration::getApplied)
                        .map(MigrationToApply::getVersion)
                        .orElse(NO_VERSION);

                // Remove all migrations related to migrationModel.getTopic()
                Loader.all(migrationModel.getTopic(),
                        Path.of(directory.getAbsolutePath()))
                    .forEach(to -> {
                        migrationFiles.removeIf(p ->
                            p.equals(to.getFile()));
                    });

                // Load newer migrations if any
                // TODO use the list of all migrations
                final var newers =
                    Loader.newer(currentVersion,
                        migrationModel.getTopic(),
                        Path.of(directory.getAbsolutePath()));

                newers.forEach(to -> {

                    // create, patch or remove?
                    final var strategy = strategyOf(to);

                    // apply the strategy
                    strategy.execute(admin);

                    //TODO Commit the applied migration

                });


            }
        }catch(IOException e) {
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "failing to read migrations: " + e.getMessage());
        }
    }
}
