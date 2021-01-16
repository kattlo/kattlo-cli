package com.github.kattlo.topic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.Reporter;
import com.github.kattlo.topic.migration.Strategy;
import com.github.kattlo.topic.yaml.Loader;
import com.github.kattlo.topic.yaml.TopicOperation;
import com.github.kattlo.topic.yaml.TopicOperationMapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    name = "topic",
    description = "Migrations for Topics",
    subcommands = {
        TopicImportCommand.class
    }
)
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
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

    private final Reporter reporter = new PrintStreamReporter(System.out);

    private File directory;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Directory with topic migrations",
        defaultValue = ".",
        required = true
    )
    public void setDirectory(File directory){
        this.directory = Objects.requireNonNull(directory);
    }
    public File getDirectory() {
        return directory;
    }

    public EntryCommand getParent() {
        return parent;
    }

    void validateOptions() {
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

            backend.init(parent.getKafkaConfiguration());

            var iterator = migrationFiles.iterator();
            while(iterator.hasNext()){
                var migrationFile = iterator.next();
                log.debug("Migration file {}", migrationFile);

                //TODO Validate yaml against schema??

                final var migrationModel =
                    Loader.load(migrationFile);

                final var operation =
                    mapper.map(migrationModel, migrationFile);

                // fetch the 'topic' latest migration
                final var latestMigration =
                    backend.current(
                        ResourceType.TOPIC,
                        operation.getTopic());

                final var currentVersion =
                    latestMigration
                        .map(Resource::getVersion)
                        .orElse(NO_VERSION);

                // Remove all migrations related to migrationModel.getTopic()
                Loader.all(migrationModel.getTopic(),
                        Path.of(directory.getAbsolutePath()))
                    .forEach(to -> {
                        log.debug("File to remove from files list: {}", to.getFile());
                        migrationFiles.removeIf(p ->
                            p.equals(to.getFile()));
                    });

                log.debug("Current files within migrationFiles {}", migrationFiles);

                // update the iterator with updated migrationFiles'
                iterator = migrationFiles.iterator();

                // Load newer migrations if any
                // TODO use the list of all migrations
                final var newers =
                    Loader.newer(currentVersion,
                        migrationModel.getTopic(),
                        Path.of(directory.getAbsolutePath()));

                // TODO May report nothing todo?
                //  when no new file found
                newers.forEach(to -> {

                    // create, patch or remove?
                    final var strategy = strategyOf(to);

                    var migration = to.toMigration();
                    reporter.report(migration);

                    // apply the strategy
                    strategy.execute(admin);

                    log.debug("Migration to commit {}", migration);

                    // commit the applied migration
                    var current = backend.commit(migration);

                    log.debug("New Topic's state {}", current);
                });

            }
        }catch(IOException e) {
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "failing to read migrations: " + e.getMessage());
        }catch(BackendException e){
            reporter.report(e);
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "general error: " + e.getMessage(), e);
        }
    }
}
