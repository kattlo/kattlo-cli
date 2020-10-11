package com.github.kattlo.topic.yaml;

import java.nio.file.Path;

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
    TopicOperation map(Loader.Model model, Path file);

    @Named("versionOf")
    default String versionOf(Path file) {
        Loader.matches(file);

        return Loader
            .versionOf(file)
            .orElse(NO_VERSION);
    }
}
