package com.github.kattlo.core.backend.file.yaml;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import com.github.kattlo.core.backend.file.yaml.model.State;
import com.github.kattlo.core.exception.LoadException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author fabiojose
 */
public final class Loader {

    private static final Constructor CONSTRUCTOR =
        new Constructor(State.class);

    private static final Yaml YAML =
        new Yaml(CONSTRUCTOR);

    public static State load(Path file) {

        try {
            return YAML.load(new FileReader(file.toFile(),
                Charset.forName("UTF-8")));
        }catch(IOException e){
            throw new LoadException(e);
        }

    }
}
