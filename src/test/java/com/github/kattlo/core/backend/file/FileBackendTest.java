package com.github.kattlo.core.backend.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.github.kattlo.core.backend.Migration2;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.backend.file.yaml.model.topic.Original;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * @author fabiojose
 */
public class FileBackendTest {

    private static final Yaml YAML = new Yaml();

    @Test
    public void should_throw_when_try_commit_null_migration() {

        assertThrows(NullPointerException.class, () ->
            new FileBackend(Path.of("./build/tmp/.file-backend.yaml"))
                .commit(null));

    }

    @SuppressWarnings("unchecked")
    //@Test
    public void should_commit_the_applied_migration_and_return_the_new_resource_state()
        throws Exception {

        // setup
        var workdir = Path.of("./src/test/resources/backend/file");

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("retention.bytes", "1024");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "7",
            "config", config
        ));
        applied.setNotes("retention.bytes and partitions");
        applied.setOperation(OperationType.PATCH);
        applied.setOriginal(original);
        applied.setResourceName("topic-name-0");
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0003");

        var backend = new FileBackend(workdir);

        // act
        var actual = backend.commit(applied);

        // assert
        assertNotNull(actual);
        assertEquals("topic-name-0", actual.getResourceName());
        assertEquals(ResourceType.TOPIC, actual.getResourceType());
        assertEquals("v0003", actual.getVersion());
        assertEquals(ResourceStatus.AVAILABLE, actual.getStatus());
        assertNotNull(actual.getTimestamp());

        var attributes = actual.getAttributes();
        assertNotNull(attributes);

        assertEquals("7", attributes.get("partitions"));

        var actualConfig = (Map<String, Object>)attributes.get("config");
        assertEquals("1024", actualConfig.get("retention.bytes"));

    }

    public void should_commit_the_applied_migration_and_patch_existing_config() {

    }

    public void should_commit_the_applied_migration_and_pacth_existing_replication_factor() {

    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_commit_the_applied_migration() throws Exception {

        // setup
        var workdir = Path.of("./build/tmp");

        var original = new Original();
        original.setContentType("text/yaml");
        original.setContent("tYmFzZTY0RmlsZUNvbnRlbnQ=");//base64FileContent
        original.setPath("/path/to/original.yaml");

        var config = Map.of("compression.type", "snappy");

        var applied = new Migration2();
        applied.setAttributes(Map.of(
            "partitions", "2",
            "replicationFactor", "1",
            "config", config
        ));
        applied.setNotes("some notes");
        applied.setOperation(OperationType.CREATE);
        applied.setOriginal(original);
        applied.setResourceName("topic-name-1");
        applied.setResourceType(ResourceType.TOPIC);
        applied.setTimestamp(LocalDateTime.now());
        applied.setVersion("v0001");

        var backend = new FileBackend(workdir);

        // act
        backend.commit(applied);

        var actual = (Map<String, Object>)
            YAML.load(new FileReader(new File(workdir.toFile(),
                ".kt-topic-name-1.yaml")));

        // assert
        assertEquals("topic-name-1", actual.get("resourceName"));
        assertEquals("TOPIC", actual.get("resourceType"));
        assertEquals("v0001", actual.get("version"));
        assertEquals("AVAILABLE", actual.get("status"));
        assertEquals("2", actual.get("partitions"));
        assertEquals("1", actual.get("replicationFactor"));
        assertTrue(actual.containsKey("timestamp"));

        assertTrue(actual.containsKey("config"));

        assertTrue(actual.containsKey("history"));
        var actualHistories = (List<Object>)actual.get("history");

        assertEquals(1, actualHistories.size());

        var actualHistory = (Map<String, Object>)actualHistories.iterator().next();

        assertTrue(actualHistory.containsKey("config"));
        assertEquals("create", actualHistory.get("operation"));
        assertTrue(actualHistory.containsKey("original"));
    }

    @Test
    public void should_return_the_current_state(){

    }

    @Test
    public void should_return_empty_current_when_no_state_file() {

    }

    @Test
    public void should_create_state_file_within_current_directory() {

    }

    @Test
    public void should_be_idempotent_when_try_to_commit_same_version() {

    }

    @Test
    public void should_return_the_migration_history() {

    }

    @Test
    public void should_return_empty_when_no_history() {

    }
}
