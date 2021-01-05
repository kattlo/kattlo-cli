package com.github.kattlo.topic.yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.kattlo.core.exception.WriteException;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@UtilityClass
@Slf4j
public class Writer {

    private static final DumperOptions OPTIONS = new DumperOptions();
    static {
        OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
        OPTIONS.setPrettyFlow(Boolean.TRUE);
        OPTIONS.setCanonical(Boolean.FALSE);
    };

    private static final Yaml YAML =
        new Yaml(OPTIONS);

    public static void write(Model model, Path file) {

        try{
            if(!file.toFile().exists()){
                Files.createFile(file);
                log.debug("File was created {}", file);
            }

            YAML.dump(model.asMap(), new FileWriter(file.toFile()));
            log.debug("Yaml dumped to  {}", file);

        }catch(IOException e) {
            throw new WriteException(e.getMessage(), e);
        }
    }
}
