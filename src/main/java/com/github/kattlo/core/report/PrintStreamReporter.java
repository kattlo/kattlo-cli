package com.github.kattlo.core.report;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.topic.TopicRuleException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class PrintStreamReporter implements Reporter {

    private static final String EMPTY = "";
    private static final String _1_SPACE = " ";
    private static final String _2_SPACE = "  ";

    private Jsonb json;

    private final PrintStream out;
    public PrintStreamReporter(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    private Jsonb getJson(){
        if(Objects.isNull(json)){
            json = JsonbBuilder.create();
        }

        return json;
    }

    @Override
    public void report(Migration migration){
        report(migration, Boolean.FALSE);
    }

    @Override
    public void report(Migration migration, boolean isImport) {
        Objects.requireNonNull(migration,
            "Provide a non-null migration instance");

        out.println(EMPTY);

        switch(migration.getOperation()){
            case CREATE:
                if(!isImport){
                    out.print("+ create");
                } else {
                    out.print("= import");
                }
                break;
            case PATCH:
                out.print("~ patch");
                break;
            case REMOVE:
                out.print("! remove");
                break;
            default:
                throw new IllegalArgumentException(migration.getResourceType().name());
        }
        out.print(_1_SPACE);
        out.println(migration.getResourceType());

        out.print(_2_SPACE);
        out.println(migration.getVersion());

        out.print(_2_SPACE);
        out.print("original ->");
        out.print(_1_SPACE);
        out.println(migration.getOriginal().getPath());

        out.print(_2_SPACE);
        out.print("resource ->");
        out.print(_1_SPACE);
        out.println(migration.getResourceName());

        Optional.ofNullable(migration.getAttributes())
            .ifPresent(attributes ->
                attributes.entrySet().stream()
                    .forEach(kv -> {
                        out.print(_2_SPACE);
                        out.print(kv.getKey());
                        out.print(_1_SPACE);
                        out.print("->");
                        out.print(_1_SPACE);
                        out.println(kv.getValue());
                    }));
    }

    @Override
    public void report(Throwable e) {
        Objects.requireNonNull(e, "Provide a non-null exception instance");

        out.println(EMPTY);
        out.println("** Error **");
        out.print(_2_SPACE);
        out.println(e.getMessage());
        e.printStackTrace(out);

    }

    @Override
    public void report(TopicRuleException e) {
        Objects.requireNonNull(e, "Provide a non-null exception instance");

        out.println();
        out.println();
        out.print(_1_SPACE);
        out.print("Topic Rule Violation:");
        out.println();
        out.println();
        e.getDetails().forEach(detail -> {
            out.print(_2_SPACE);
            out.print("-");
            out.print(_1_SPACE);
            out.print(detail);
            out.println();
        });
        out.println();

        out.print(_1_SPACE);
        out.print("File:");
        out.print(_1_SPACE);
        out.print(e.getFile());
        out.println();
        out.println();

    }

    private void currentPlain(Resource resource) {

        out.println(resource.getStatus().name());
        out.print(resource.getResourceType().name());
        out.print(_1_SPACE);
        out.print(":");
        out.print(_1_SPACE);
        out.println(resource.getResourceName());

        out.print("version");
        out.print(":");
        out.print(_1_SPACE);
        out.println(resource.getVersion());

        Optional.ofNullable(resource.getAttributes())
            .ifPresent(attributes ->
                attributes.entrySet().stream()
                    .sorted((v1, v2) -> v1.getKey().compareTo(v2.getKey()))
                    .forEach(kv -> {
                        out.print(_2_SPACE);
                        out.print(kv.getKey());
                        out.print(_1_SPACE);
                        out.print("->");
                        out.print(_1_SPACE);
                        out.println(kv.getValue());
                    }));
    }

    private void currentJson(Resource resource) {

        log.debug("To serialize as json current state {}", resource);
        getJson().toJson(resource, out);

    }

    @Override
    public void current(Resource resource, ReportFormat format) {
        Objects.requireNonNull(resource, "Provide a not null resource");
        Objects.requireNonNull(format, "Provide a not null format");

        if(ReportFormat.PLAIN.equals(format)){
            currentPlain(resource);
        } else if(ReportFormat.JSON.equals(format)){
            currentJson(resource);
        } else {
            throw new IllegalArgumentException(format.name());
        }
    }

    public void historyPlain(Stream<Migration> migrations) {

        var sorted =
          migrations
            .sorted((m1, m2) -> m2.getVersion().compareTo(m1.getVersion()))
            .collect(Collectors.toList());

        if(!sorted.isEmpty()){
            var latest = sorted.iterator().next();

            out.print(latest.getResourceType());
            out.print(":");
            out.print(_1_SPACE);
            out.println(latest.getResourceName());
            out.println();

            sorted.forEach(m -> {
                out.print(_2_SPACE);
                out.print(m.getVersion());
                out.print(_1_SPACE);
                out.print("->");
                out.print(_1_SPACE);
                out.print(m.getOperation().name());
                out.println();

                out.print(_2_SPACE);
                out.println(m.getTimestamp());

                out.print(_2_SPACE);
                out.println(m.getNotes());

                out.print(_2_SPACE);
                out.println(m.getOriginal().getPath());

                m.getAttributes().entrySet().forEach(a -> {
                    out.print(_2_SPACE);
                    out.print(_2_SPACE);
                    out.print(a.getKey());
                    out.print(_1_SPACE);
                    out.print("->");
                    out.print(_1_SPACE);
                    out.print(a.getValue());
                    out.println();
                });

                out.println();
                out.println();
            });
        } else {
            out.println();
            out.print("**WARNING**");
            out.println();
            out.println();
            out.print(_2_SPACE);
            out.print("There is no history to show.");
            out.println();
        }
    }

    public void historyJson(Stream<Migration> migrations) {

        var sorted =
          migrations
            .sorted((m1, m2) -> m2.getVersion().compareTo(m1.getVersion()))
            .collect(Collectors.toList());

        if(!sorted.isEmpty()){
            var latest = sorted.iterator().next();

            var model = Map.of(
                latest.getResourceType().name(), latest.getResourceName(),
                "history", sorted
            );

            getJson().toJson(model, out);
        } else {
            getJson().toJson(Map.of(), out);
        }
    }

    @Override
    public void history(Stream<Migration> migrations, ReportFormat format) {
        Objects.requireNonNull(migrations, "Provide a not null migrations");
        Objects.requireNonNull(format, "Provide a not null format");

        if(ReportFormat.PLAIN.equals(format)){
            historyPlain(migrations);
        } else if(ReportFormat.JSON.equals(format)){
            historyJson(migrations);
        } else {
            throw new IllegalArgumentException(format.name());
        }
    }

    @Override
    public void generated(Path path){

        out.println();
        out.println();
        out.print("Generated at:");
        out.print(_1_SPACE);
        out.print(path);
        out.println();
        out.println();

    }

    @Override
    public void uptodate() {

        out.println();
        out.println();
        out.print("Everything is up to date.");
        out.println();
        out.println();
    }

    @Override
    public void initialized(Path path) {

        out.println();
        out.print("Initialized at");
        out.print(_1_SPACE);
        out.print(path);
        out.println();

    }
}
