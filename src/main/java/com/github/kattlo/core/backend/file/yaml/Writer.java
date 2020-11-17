package com.github.kattlo.core.backend.file.yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

import com.github.kattlo.core.backend.file.yaml.model.topic.State;
import com.github.kattlo.core.exception.WriteException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * @author fabiojose
 */
public final class Writer {

    private static final Constructor CONSTRUCTOR =
        new Constructor(State.class);

    private static final Representer REPRESENTER =
        new Representer();
    static {
        REPRESENTER.addClassTag(State.class, Tag.MAP);
    };

    private static final Yaml YAML =
        new Yaml(CONSTRUCTOR, REPRESENTER);

    public static void write(final State state, final Path file) {
        Objects.requireNonNull(state);

        try{
            final var output = new FileWriter(file.toFile(),
                Charset.forName("UTF-8"));

            YAML.dump(state, output);

        }catch(IOException e) {
            throw new WriteException(e.getMessage(), e);
        }
    }
}
