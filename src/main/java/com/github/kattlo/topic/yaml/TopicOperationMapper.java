package com.github.kattlo.topic.yaml;

import java.nio.file.Path;

import com.github.kattlo.core.yaml.MigrationLoader;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author fabiojose
 */
@Mapper(componentModel = "cdi")
public interface TopicOperationMapper {

    String NO_VERSION = null;

    @Mapping(target = "version", source = "file", qualifiedByName = "versionOf")
    @Mapping(target = "file", source = "file")
    TopicOperation map(Model model, Path file);

    @Named("versionOf")
    default String versionOf(Path file) {
        MigrationLoader.matches(file);

        return MigrationLoader
            .versionOf(file)
            .orElse(NO_VERSION);
    }
}
