package com.github.kattlo.core.report;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

import com.github.kattlo.core.backend.Migration;

/**
 * @author fabiojose
 */
public class PrintStreamReporter implements Reporter {
    /*
    + create a TOPIC
      v0001
        original -> /path/to/migration.yaml
        resource -> topic-name
        att1 -> vlu1
        att1 -> vlu2
    */

    private final PrintStream out;
    public PrintStreamReporter(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void report(Migration migration) {
        Objects.requireNonNull(migration,
            "Provide a non-null migration instance");

        switch(migration.getOperation()){
            case CREATE:
                out.println("+ create");
                break;
            case PATCH:
                out.println("~ patch");
                break;
            case REMOVE:
                out.println("! remove");
                break;
            default:
                throw new IllegalArgumentException(migration.getResourceType().name());
        }

        out.print("  ");
        out.println(migration.getVersion());

        out.print("  ");
        out.print("original ->");
        out.print(" ");
        out.println(migration.getOriginal().getPath());

        out.print("  ");
        out.print("resource ->");
        out.print(" ");
        out.println(migration.getResourceName());

        Optional.ofNullable(migration.getAttributes())
            .ifPresent(attributes ->
                attributes.entrySet().stream()
                    .forEach(kv -> {
                        out.print("  ");
                        out.print(kv.getKey());
                        out.print(" ");
                        out.print("->");
                        out.print(" ");
                        out.println(kv.getValue());
                    }));
    }

}
