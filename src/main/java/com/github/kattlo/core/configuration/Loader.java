package com.github.kattlo.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.github.kattlo.core.backend.ResourceType;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author fabiojose
 */
public class Loader {

    private static Yaml YAML;

    private static Yaml getYaml() {
        if(null== YAML){
            Constructor constructor = new Constructor(Map.class);
            YAML = new Yaml(constructor);
        }

        return YAML;
    }

    public static Optional<Map<String, Object>> load(File configuration, ResourceType type)
            throws IOException {

        return getYaml().load(new FileReader(configuration));
    }
}
