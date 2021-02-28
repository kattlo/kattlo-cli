package com.github.kattlo.topic.yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.kattlo.core.exception.LoadException;
import com.github.kattlo.core.yaml.MigrationLoader;

import org.mapstruct.factory.Mappers;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@UtilityClass
@Slf4j
public class Loader {

    private static final Constructor CONSTRUCTOR =
        new Constructor(Model.class);

    private static final Yaml YAML =
        new Yaml(CONSTRUCTOR);

    private static final TopicOperationMapper MAPPER =
        Mappers.getMapper(TopicOperationMapper.class);

    /**
     * @throws LoadException When any problem happens to load the file
     * @throws IllegalArgumentException When the file name does not follow the pattern {@link #FILE_NAME_PATTERN}
     */
    public static Model load(Path file) {
        MigrationLoader.matches(file);

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
            MigrationLoader.list(directory)
                .map(file ->
                    MAPPER.map(load(file), file))
                .filter(o -> o.getTopic().equals(topic))
                .sorted(Comparator.comparing(TopicOperation::getVersion))
                .filter(o -> o.getVersion().compareTo(currentVersion) > 0)
                .findFirst();
    }

    public static Stream<TopicOperation> newer(
            final String currentVersion,
            final String topic,
            final Path directory) throws IOException {

        return
            MigrationLoader.list(directory)
                .filter(file -> MigrationLoader.greater(file, currentVersion))
                .map(file ->
                    MAPPER.map(load(file), file))
                .filter(o -> o.getTopic().equals(topic))
                .sorted(Comparator.comparing(TopicOperation::getVersion))
                .filter(o -> o.getVersion().compareTo(currentVersion) > 0)
                .peek(o -> log.debug("Newer TopicOperation {}", 0));
    }

    public static Stream<TopicOperation> all(final String topic,
        final Path directory) throws IOException {

        return
            MigrationLoader.list(directory)
                .map(file -> MAPPER.map(load(file), file))
                .filter(o -> o.getTopic().equals(topic))
                .sorted(Comparator.comparing(TopicOperation::getVersion))
                .peek(o -> log.debug("TopicOperation loaded {}", o));

    }

}
