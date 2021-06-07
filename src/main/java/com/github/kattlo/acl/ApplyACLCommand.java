package com.github.kattlo.acl;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.github.kattlo.SharedOptionValues;
import com.github.kattlo.acl.migration.Strategy;
import com.github.kattlo.acl.yaml.ACLMigration;
import com.github.kattlo.acl.yaml.Loader;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.BackendException;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.kafka.Kafka;
import com.github.kattlo.core.report.PrintStreamReporter;
import com.github.kattlo.core.report.Reporter;
import com.github.kattlo.core.yaml.MigrationLoader;

import org.everit.json.schema.ValidationException;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 * @author fabiojose
 */
@Command(
    name = "acl",
    description = "Apply ACL Migrations",
    showDefaultValues = true,
    mixinStandardHelpOptions = true
)
@Slf4j
public class ApplyACLCommand implements Runnable {

    private static final String NO_VERSION = "v0000";

    private File directory;

    @Spec
    CommandSpec spec;

    @Inject
    Backend backend;

    @Inject
    Kafka kafka;

    // TODO use dependency injection
    Reporter reporter = new PrintStreamReporter(System.out);

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Directory with ACL migrations",
        defaultValue = ".",
        required = true
    )
    public void setDirectory(File directory){
        this.directory = Objects.requireNonNull(directory);
    }
    public File getDirectory() {
        return directory;
    }

    private void validateOptions() {
        SharedOptionValues.validateOptions();

        if(!directory.canRead()){
            throw new CommandLine.
                ParameterException(spec.commandLine(),
                        directory + " not found or does not have right to read");
        }
    }

    @Override
    public void run() {
        validateOptions();

        var kafkaProperties = SharedOptionValues.getKafkaConfiguration();
        try(final var admin = kafka.adminFor(kafkaProperties)) {

            backend.init(kafkaProperties);

            var files = MigrationLoader.list(directory)
                .sorted()
                .collect(Collectors.toList());
            var iterator = files.iterator();
            while(iterator.hasNext()){
                var file = iterator.next();
                log.debug("ACL migration file: {}", file);

                var migrationAsMap = Loader.loadAsMap(file);
                log.debug("ACL migration as Java Map {}", migrationAsMap);

                var migrationAsJSON = MigrationLoader.parseJson(migrationAsMap);
                Loader.validade(migrationAsJSON);
                log.debug("ACL migration file synthax OK!");

                var migration = new ACLMigration(migrationAsJSON, file);

                // Fetch the latest ACL migration by Principal Name
                var latestMigration = backend
                    .current(ResourceType.ACL, migration.getPrincipal());

                var currentVersion = latestMigration
                    .map(Resource::getVersion)
                    .orElseGet(() -> NO_VERSION);
                log.debug("Current version {}", currentVersion);

                // load migrations by principal name
                var migrations = Loader.allByPrincipal(migration.getPrincipal(),
                    directory.toPath());

                // keep just the newer migrations if any
                var newers = migrations.peek(m -> {
                        log.debug("File to remove from files list: {}", m.getFile().toAbsolutePath());
                        files.removeIf(f -> f.equals(m.getFile().toAbsolutePath()));
                    })
                    .filter(m -> m.getVersion().compareTo(currentVersion) > 0)
                    .sorted()
                    .collect(Collectors.toList());
                log.debug("Remaining files to process in the next file system loop {}", files);

                // update the iterator with new list of files in the main loop
                iterator = files.iterator();
                log.debug("Migrations to apply {}", newers);

                if(newers.isEmpty()){
                    reporter.uptodate();
                } else {
                    newers.forEach(newer-> {
                        log.debug("ACL migration to apply {}", newer);

                        final var strategy = Strategy.of(newer.getJson());

                        // JSONObject to Migration
                        var forBackend = newer.toBackend();
                        reporter.report(forBackend);

                        strategy.execute(admin);

                        // commit the applied migration
                        var current = backend.commit(forBackend);

                        log.debug("New ACL state {}", current);
                    });
                }
            }

        }catch(IOException e){
            log.error(e.getMessage(), e);
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "failing to read migrations: " + e.getMessage());
        }catch(ValidationException e){
            log.error(e.getMessage(), e);
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "Does not follow the schema: " + e.getErrorMessage());
            // TODO Better synthax check messages
        }catch(BackendException e){
            log.error(e.getMessage(), e);
            reporter.report(e);
            throw new CommandLine.ExecutionException(spec.commandLine(),
                "general error: " + e.getMessage(), e);
        }
    }

}
