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

    /*
    ** Error **
        <message>
        <details>
    */


    private static final String EMPTY = "";
    private static final String _1_SPACE = " ";
    private static final String _2_SPACE = "  ";

    private final PrintStream out;
    public PrintStreamReporter(PrintStream out) {
        this.out = Objects.requireNonNull(out);
    }

    @Override
    public void report(Migration migration) {
        Objects.requireNonNull(migration,
            "Provide a non-null migration instance");

        out.println(EMPTY);

        switch(migration.getOperation()){
            case CREATE:
                out.print("+ create");
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
}
