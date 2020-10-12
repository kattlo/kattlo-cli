package com.github.kattlo.topic.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.factory.Mappers;
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

    private static final TopicOperationMapper MAPPER =
        Mappers.getMapper(TopicOperationMapper.class);

    public static final Pattern VERSION_PATTERN =
        Pattern.compile("(v[0-9]{4})");

    public static final Pattern FILE_NAME_PATTERN =
        Pattern.compile("v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml");

    public static final Pattern FILE_EXT_PATTERN =
        Pattern.compile(".*\\.ya?ml");

    static Stream<Path> list(final Path directory) throws IOException {
        return Files.list(directory)
            .filter(f ->
                FILE_EXT_PATTERN.matcher(
                    f.getFileName().toString())
                        .matches());
    }

    public static Stream<Path> list(final File directory) throws IOException {
        return list(Path.of(directory.getAbsolutePath()));
    }

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
     * @throws LoadException When any problem happens to load the file
     * @throws IllegalArgumentException When the file name does not follow the pattern {@link #FILE_NAME_PATTERN}
     */
    public static Model load(Path file) {
        matches(file);

        try {
            return YAML.load(new FileReader(file.toFile()));
        }catch(FileNotFoundException e){
            throw new LoadException(e);
        }
    }

    /**
     * Search for the next migration file version from the current one
     * @throws LoadException When any problem happen during the search
     */
    public static Optional<TopicOperation> next(
            final String currentVersion,
            final String topic,
            final Path directory) throws IOException {

        return
            list(directory)
                .map(file ->
                    MAPPER.map(load(file), file))
                .filter(o -> o.getTopic().equals(topic))
                .sorted(Comparator.comparing(TopicOperation::getVersion))
                .filter(o -> o.getVersion().compareTo(currentVersion) > 0)
                .findFirst();

    }

    @Data
    public static class Model {
        private String operation;
        private String notes;
        private String topic;
        private Integer partitions;
        private Integer replicationFactor;

        private Map<String, Object> config;
    }
}
