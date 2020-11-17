package com.github.kattlo.core.backend.file.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
            return YAML.load(new FileReader(file.toFile()));
        }catch(FileNotFoundException e){
            throw new LoadException(e);
        }

    }
}
