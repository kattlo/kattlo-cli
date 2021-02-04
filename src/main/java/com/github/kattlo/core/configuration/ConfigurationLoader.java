package com.github.kattlo.core.configuration;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.kattlo.core.backend.ResourceType;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author fabiojose
 */
public class ConfigurationLoader {

    private static Yaml YAML;

    private static Yaml getYaml() {
        if(null== YAML){
            Constructor constructor = new Constructor(Map.class);
            YAML = new Yaml(constructor);
        }

        return YAML;
    }

    @SuppressWarnings("unchecked")
    public static Optional<Map<String, Object>> load(File configuration, ResourceType type)
            throws IOException {
        Objects.requireNonNull(configuration, "provide a non-null configuration arg");
        Objects.requireNonNull(type, "provide a non-null type arg");

        return ofNullable(getYaml().load(new FileReader(configuration)))
            .map(m -> (Map<String, Object>)m)
            .map(c -> c.get("rules"))
            .filter(Objects::nonNull)
            .map(r -> (Map<String, Object>)r)
            .map(r -> r.get(type.name().toLowerCase()))
            .filter(Objects::nonNull)
            .map(t -> (Map<String, Object>)t);

    }
}
