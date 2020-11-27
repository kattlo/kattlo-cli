package com.github.kattlo.core.backend.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.kattlo.core.backend.Backend2;
import com.github.kattlo.core.backend.Migration2;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.exception.LoadException;
import com.github.kattlo.core.exception.WriteException;

import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages the state using a file in the local disc storage
 *
 * @author fabiojose
 */
@Slf4j
public class FileBackend implements Backend2 {

    private static final Yaml YAML = new Yaml();

    private static final Map<ResourceType, String> FILE_NAMES = Map.of(
        ResourceType.TOPIC, ".kt-%s.yaml"
    );

    private final Path workdir;

    public FileBackend(Path workdir) {
        this.workdir = Objects.requireNonNull(workdir);
    }

    private File file(ResourceType type, String name) {

        var fileName = String.format(FILE_NAMES.get(type), name);
        return new File(workdir.toFile(), fileName);

    }

    private Optional<Map<String, Object>> load(ResourceType type, String name) {

        var filePath = file(type, name);

        log.debug("Try to load from disc: {}", filePath);

        if(filePath.exists()){
            log.trace("File exists");
            try{
                var model = YAML.load(new FileReader(filePath,
                    Charset.forName("UTF-8")));
                log.debug("Parsed model: {}", model);

                @SuppressWarnings("unchecked")
                var asMap = (Map<String, Object>)model;

                return Optional.of(asMap);
            }catch(IOException e){
                throw new LoadException(e.getMessage(), e);
            }
        }

        log.trace("File does not exists");

        return Optional.empty();
    }

    private void write(Map<String, Object> newState, ResourceType type,
            String name) {

        var file = file(type, name);
        log.debug("File to write down the state: {}", file);

        try{
            YAML.dump(newState, new FileWriter(file,
                Charset.forName("UTF-8")));

            log.debug("State written");
        }catch(IOException e){
            log.debug("Unable to write the state", e);
            throw new WriteException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> topicFullJoin(Map<String, Object> left, Migration2 right){

        var attributesLeft = new HashMap<String, Object>();
        attributesLeft.putAll(Optional
            .ofNullable(left.get("attributes"))
            .map(a -> (Map<String, Object>)a)
            .orElse(new HashMap<String, Object>()));

        var attributesRight = new HashMap<String, Object>();
        attributesRight.putAll(right.getAttributes());

        var leftCopy = new HashMap<String, Object>();
        leftCopy.putAll(left);
        leftCopy.put("attributes", attributesLeft);

        var rightCopy = new HashMap<String, Object>();
        rightCopy.putAll(right.asMigrationMap());
        rightCopy.remove("attributes");
        rightCopy.remove("original");

        //config
        var leftConfig = Optional.ofNullable(attributesLeft.get("config"))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Left config: {}", leftConfig);

        var rightConfig = Optional.ofNullable(attributesRight.get("config"))
            .map(c -> (Map<String, Object>)c)
            .orElse(Map.of());
        log.debug("Right config: {}", rightConfig);

        var newConfig = new HashMap<String, Object>();
        newConfig.putAll(leftConfig);
        newConfig.putAll(rightConfig);
        log.debug("Joined config: {}", newConfig);

        // remove joined attribute
        attributesLeft.remove("config");
        attributesRight.remove("config");

        var joined = new HashMap<String, Object>();
        joined.putAll(leftCopy);
        joined.putAll(rightCopy);
        joined.put("config", newConfig);
        log.debug("Joined migration: {}", joined);

        return joined;
    }

    @Override
    public Resource commit(Migration2 applied) {
        Objects.requireNonNull(applied);
        log.debug("Try to commit: {}", applied);

        // TODO Load the current state
        var currentState = load(applied.getResourceType(), applied.getResourceName())
            .orElse(Map.of());

        // TODO Create new state in memory by outer-join (aka full-join)
        var newState = topicFullJoin(currentState, applied);

        // TODO Append to history
        @SuppressWarnings("unchecked")
        var history = Optional.ofNullable(newState.get("history"))
            .map(l -> (List<Object>)l)
            .orElse(new ArrayList<>());
        history.add(applied.asMigrationMap());
        newState.put("history", history);

        // TODO Write to disc
        write(newState, applied.getResourceType(), applied.getResourceName());
        //TODO Falta o status

        return null;
    }

    @Override
    public Optional<Resource> current(ResourceType type, String name) {
        return null;
    }

    @Override
    public Stream<Migration2> history(ResourceType type, String name) {
        return null;
    }

}
