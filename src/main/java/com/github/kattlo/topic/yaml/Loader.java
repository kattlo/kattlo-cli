package com.github.kattlo.topic.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.Data;

/**
 * @author fabiojose
 */
public class Loader {

    private static final Constructor CONSTRUCTOR =
        new Constructor(Model.class);

    private static final Yaml YAML =
        new Yaml(CONSTRUCTOR);

    public static final Pattern VERSION_PATTERN =
        Pattern.compile("(v[0-9]{4})");

    public static final Pattern FILE_NAME_PATTERN =
        Pattern.compile("v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml");

    /**
     * @throws IllegalArgumentException When the file name does not follow the pattern {@link #FILE_NAME_PATTERN}
     */
    public static void matches(Path file) {
        if(!FILE_NAME_PATTERN.matcher(file.getFileName().toString()).matches()){
            throw new IllegalArgumentException(file.getFileName().toString());
        }
    }

    public static Optional<String> versionOf(Path file){

        final Matcher m =
            VERSION_PATTERN.matcher(file.getFileName().toString());

        return
            Optional.of(m.find())
                .filter(found -> found)
                .map(f -> m.group());

    }

    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException When the file path does not exists
     * @throws IllegalArgumentException When the file name does not follow the pattern {@link #FILE_NAME_PATTERN}
     */
    public static Model load(Path file) throws FileNotFoundException {
        matches(file);

        return YAML.load(new FileReader(file.toFile()));

    }

    @Data
    public static class Model {
        private String operation;
        private String notes;
        private String topic;
        private int partitions;
        private int replicationFactor;

        private Map<String, Object> config;
    }
}
