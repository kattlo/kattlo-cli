package com.github.kattlo.acl.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import com.github.kattlo.core.exception.LoadException;
import com.github.kattlo.core.yaml.MigrationLoader;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.yaml.snakeyaml.Yaml;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@UtilityClass
@Slf4j
public class Loader {

    private static final String SCHEMA_PATH = "/schema/acl/by-principal.schema.json";

    private static Yaml YAML;
    private static Schema SCHEMA;

    private static Yaml getYaml() {
        if(null== YAML){
            YAML = new Yaml();
        }

        return YAML;
    }

    private static Schema getSchema() {
        if(Objects.isNull(SCHEMA)) {
            try(var input = Loader.class.getResourceAsStream(SCHEMA_PATH)){

                var rawSchema = new JSONObject(new JSONTokener(input));

                SCHEMA = SchemaLoader.builder()
                    .schemaJson(rawSchema)
                    .draftV7Support()
                    .build()
                    .load()
                    .build();

                log.debug("Schema loaded: {}", SCHEMA_PATH);

            } catch (IOException e){
                throw new LoadException(e.getMessage(), e);
            }
        }

        return SCHEMA;
    }

    /**
     * @throws LoadException When any problem happens to load the file
     * @throws IllegalArgumentException When the file name does not follow the pattern
     * {@link MigrationLoader#FILE_NAME_PATTERN}
     */
    public static Map<String, Object> loadAsMap(Path yaml) {
        MigrationLoader.matches(yaml);

        try {
            return getYaml().load(new FileReader(yaml.toFile()));
        }catch(FileNotFoundException e){
            throw new LoadException(e);
        }
    }

    /**
     * @throws LoadException When any problem happens during the json validation
     */
    public static void validade(JSONObject json) {

        var schema = getSchema();
        schema.validate(json);

    }

}
