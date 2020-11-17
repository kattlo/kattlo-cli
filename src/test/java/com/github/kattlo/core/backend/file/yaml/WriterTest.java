package com.github.kattlo.core.backend.file.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.github.kattlo.core.backend.file.yaml.model.Current;
import com.github.kattlo.core.backend.file.yaml.model.History;
import com.github.kattlo.core.backend.file.yaml.model.Original;
import com.github.kattlo.core.backend.file.yaml.model.State;
import com.github.kattlo.core.exception.WriteException;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class WriterTest {

    @Test
    public void should_write_the_state_to_path() throws Exception {

        var file = Path.of("./build/tmp/.kattlo-topics-0.yaml");

        var original = new Original();
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setContentType("text/yaml");
        original.setPath("/path/to/original.yaml");

        var history = new History();
        history.setConfig(Map.of("segment.bytes", 1024));
        history.setNotes("notes");
        history.setOperation("create");
        history.setOriginal(original);
        history.setPartitions(3);
        history.setReplicationFactor(1);
        history.setTimestamp("2020-09-03T05:37:37Z");
        history.setVersion("v0001");

        var current = new Current();
        current.setConfig(Map.of("segment.bytes", 1024));
        current.setHistory(List.of(history));
        current.setPartitions(3);
        current.setReplicationFactor(1);
        current.setStatus("available");
        current.setTimestamp("2020-09-03T05:37:37Z");
        current.setTopic("topic-name");
        current.setVersion("v0001");

        var state = new State();
        state.setTopics(List.of(current));

        // act
        Writer.write(state, file);

        // assert
        var actual = (Map<String, Object>)new Yaml().load(
                new FileReader(file.toFile()));

        assertTrue(actual.containsKey("topics"));

        var states = (List<Object>)actual.get("topics");

        assertEquals(1, states.size());

        var actualState = (Map<String, Object>)states.iterator().next();

        assertEquals("topic-name", actualState.get("topic"));
        assertEquals("v0001", actualState.get("version"));

        assertTrue(actualState.containsKey("history"));
        assertTrue(actualState.containsKey("config"));
        assertTrue(actualState.containsKey("partitions"));
        assertTrue(actualState.containsKey("replicationFactor"));
        assertTrue(actualState.containsKey("status"));
        assertTrue(actualState.containsKey("timestamp"));

        var actualHistories = (List<Object>)actualState.get("history");

        assertEquals(1, actualHistories.size());

        var actualHistory = (Map<String, Object>)actualHistories.iterator().next();

        assertTrue(actualHistory.containsKey("config"));
        assertEquals("create", actualHistory.get("operation"));
        assertTrue(actualHistory.containsKey("original"));
    }

    @Test
    public void should_override_the_state_when_already_exists() throws Exception {

        var file = Path.of("./build/tmp/.kattlo-topics-1.yaml");

        var original = new Original();
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setContentType("text/yaml");
        original.setPath("/path/to/original.yaml");

        var history = new History();
        history.setConfig(Map.of("segment.bytes", 1024));
        history.setNotes("notes");
        history.setOperation("create");
        history.setOriginal(original);
        history.setPartitions(3);
        history.setReplicationFactor(1);
        history.setTimestamp("2020-09-03T05:37:37Z");
        history.setVersion("v0001");

        var current = new Current();
        current.setConfig(Map.of("segment.bytes", 1024));
        current.setHistory(List.of(history));
        current.setPartitions(3);
        current.setReplicationFactor(1);
        current.setStatus("available");
        current.setTimestamp("2020-09-03T05:37:37Z");
        current.setTopic("topic-name");
        current.setVersion("v0001");

        var state = new State();
        state.setTopics(List.of(current));

        Writer.write(state, file);

        // act
        current.setVersion("v0002");
        Writer.write(state, file);

        // assert
        var actual = (Map<String, Object>)new Yaml().load(
                new FileReader(file.toFile()));

        assertTrue(actual.containsKey("topics"));

        var states = (List<Object>)actual.get("topics");

        assertEquals(1, states.size());

        var actualState = (Map<String, Object>)states.iterator().next();

        assertEquals("topic-name", actualState.get("topic"));
        assertEquals("v0002", actualState.get("version"));

        assertTrue(actualState.containsKey("history"));
        assertTrue(actualState.containsKey("config"));
        assertTrue(actualState.containsKey("partitions"));
        assertTrue(actualState.containsKey("replicationFactor"));
        assertTrue(actualState.containsKey("status"));
        assertTrue(actualState.containsKey("timestamp"));

        var actualHistories = (List<Object>)actualState.get("history");

        assertEquals(1, actualHistories.size());

        var actualHistory = (Map<String, Object>)actualHistories.iterator().next();

        assertTrue(actualHistory.containsKey("config"));
        assertEquals("create", actualHistory.get("operation"));
        assertTrue(actualHistory.containsKey("original"));
    }

    @Test
    public void should_throw_when_invalid_path() throws Exception {

        var file = Path.of("/an/invalid/path/.kattlo-topics-2.yaml");

        var original = new Original();
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setContentType("text/yaml");
        original.setPath("/path/to/original.yaml");

        var history = new History();
        history.setConfig(Map.of("segment.bytes", 1024));
        history.setNotes("notes");
        history.setOperation("create");
        history.setOriginal(original);
        history.setPartitions(3);
        history.setReplicationFactor(1);
        history.setTimestamp("2020-09-03T05:37:37Z");
        history.setVersion("v0001");

        var current = new Current();
        current.setConfig(Map.of("segment.bytes", 1024));
        current.setHistory(List.of(history));
        current.setPartitions(3);
        current.setReplicationFactor(1);
        current.setStatus("available");
        current.setTimestamp("2020-09-03T05:37:37Z");
        current.setTopic("topic-name");
        current.setVersion("v0001");

        var state = new State();
        state.setTopics(List.of(current));

        // act
        assertThrows(WriteException.class, () ->
            Writer.write(state, file));
    }
}
