package com.github.kattlo.topic.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.Data;

/**
 * @author fabiojose
 */
public class Loader {

    private static final Constructor CONSTRUCTOR =
        new Constructor(Model.class);

    private static final TypeDescription DESCRIPTION =
        new TypeDescription(Model.class);

    static {
        //DESCRIPTION.addPropertyParameters("config", Map.class);
        //CONSTRUCTOR.addTypeDescription(DESCRIPTION);
    };

    private static final Yaml YAML =
        new Yaml(CONSTRUCTOR);

    public static final Pattern FILE_NAME_PATTERN =
        Pattern.compile("v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml");

    public static Model load(Path file) throws FileNotFoundException {

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
