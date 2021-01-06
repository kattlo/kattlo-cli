package com.github.kattlo.topic.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TopicOperationMapperTest {

    @Test
    public void should_map_the_model_to_topic_operation() throws Exception {

        final String fileName = "./src/test/resources/topics/v0001_create2.yaml";
        final Path file = Path.of(fileName);

        Model model =
            Loader.load(file);

        TopicOperation actual =
            Mappers.getMapper(TopicOperationMapper.class).map(model, file);

        assertEquals("v0001", actual.getVersion());
        assertEquals("create", actual.getOperation());
    }

}
