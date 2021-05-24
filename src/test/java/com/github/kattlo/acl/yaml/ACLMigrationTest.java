package com.github.kattlo.acl.yaml;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsMapContaining.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.ResourceType;
import com.github.kattlo.core.yaml.MigrationLoader;
import com.github.kattlo.util.MyMap;
import com.github.kattlo.util.VersionUtil;

import org.junit.jupiter.api.Test;

public class ACLMigrationTest {

    @Test
    void should_return_create_operation_when_its_create() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        assertEquals("create", acl.getOperation());

    }

    @Test
    void should_return_patch_operation_when_its_patch() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_patch.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        assertEquals("patch", acl.getOperation());
    }

    @Test
    void should_return_remove_operation_when_its_remove() {

        // TODO remove yaml schema needs revision

    }

    @Test
    void should_get_empty_when_there_is_not_notes() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-without-notes.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        assertTrue(acl.getNotes().isEmpty());
    }

    @Test
    void should_get_allow_when_has_it() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        assertTrue(acl.getAllow().isPresent());
    }

    @Test
    void should_get_deny_when_has_it() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        assertTrue(acl.getDeny().isPresent());
    }

    @Test
    void should_transform_create_to_basic_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = acl.toBackend();

        assertEquals("v0001", actual.getVersion());
        assertEquals(OperationType.CREATE, actual.getOperation());
        assertEquals("Notes about this ACL creation by Principal\n", actual.getNotes());
        assertEquals(ResourceType.ACL, actual.getResourceType());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown", actual.getResourceName());
        assertNotNull(actual.getTimestamp());
        assertEquals(VersionUtil.appVersion(), actual.getKattlo());

    }

    @Test
    void should_transform_create_to_allow_attributes_producer_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // producer
        assertThat(wrap.unbox(), hasKey("producer"));
        var producer = wrap.unfold("producer");

        assertThat(producer.unbox(), hasKey("topic"));
        var topic = producer.unfold("topic", String.class);
        assertThat(topic.unbox(), hasEntry("name", "topic-as-producer"));

        assertThat(producer.unbox(), hasKey("transactional"));
        var transactional = producer.unfold("transactional", String.class);
        assertThat(transactional.unbox(), hasEntry("id", "transactional.id-as-producer"));

    }

    @Test
    void should_transform_create_to_allow_attributes_consumer_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // consumer
        assertThat(wrap.unbox(), hasKey("consumer"));
        var consumer = wrap.unfold("consumer");

        assertThat(consumer.unbox(), hasKey("topic"));
        var topic = consumer.unfold("topic", String.class);
        assertThat(topic.unbox(), hasEntry("name", "topic-as-consumer"));

        assertThat(consumer.unbox(), hasKey("group"));
        var group = consumer.unfold("group", String.class);
        assertThat(group.unbox(), hasEntry("id", "group.id-as-consumer"));

    }

    @Test
    void should_transform_create_to_allow_attributes_topic_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // topic
        assertThat(wrap.unbox(), hasKey("topic"));
        var topic = wrap.unfold("topic");

        assertThat(topic.unbox(), hasKey("name"));
        String name = topic.get("name");
        assertEquals("my-topic-name", name);

        assertThat(topic.unbox(), hasKey("operations"));
        List<String> operations = topic.get("operations");
        assertThat(operations, hasItems("Read", "Write", "Describe"));

    }

    @Test
    void should_transform_create_to_allow_attributes_group_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // group
        assertThat(wrap.unbox(), hasKey("group"));
        var group = wrap.unfold("group");

        assertThat(group.unbox(), hasKey("id"));
        String id = group.get("id");
        assertEquals("my-group.id", id);

        assertThat(group.unbox(), hasKey("operations"));
        List<String> operations = group.get("operations");
        assertThat(operations, hasItems("Read"));

    }

    @Test
    void should_transform_create_to_allow_attributes_cluster_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // cluster
        assertThat(wrap.unbox(), hasKey("cluster"));
        var cluster = wrap.unfold("cluster");

        assertThat(cluster.unbox(), hasKey("operations"));
        List<String> operations = cluster.get("operations");
        assertThat(operations, hasItems("All"));

    }

    @Test
    void should_transform_create_to_allow_attributes_transactional_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // transactional
        assertThat(wrap.unbox(), hasKey("transactional"));
        var transactional = wrap.unfold("transactional");

        assertThat(transactional.unbox(), hasKey("id"));
        String id = transactional.get("id");
        assertEquals("my-transactional.id", id);

        assertThat(transactional.unbox(), hasKey("operations"));
        List<String> operations = transactional.get("operations");
        assertThat(operations, hasItems("All"));

    }

    @Test
    void should_transform_create_to_allow_attributes_connection_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("allow");

        // connection
        assertThat(wrap.unbox(), hasKey("connection"));
        var transactional = wrap.unfold("connection");

        assertThat(transactional.unbox(), hasKey("from"));
        List<String> from = transactional.get("from");
        assertThat(from, hasItems("172.16.0.100"));

    }

    @Test
    void should_transform_create_to_deny_attributes_producer_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // producer
        assertThat(wrap.unbox(), hasKey("producer"));
        var producer = wrap.unfold("producer");

        assertThat(producer.unbox(), hasKey("topic"));
        var topic = producer.unfold("topic", String.class);
        assertThat(topic.unbox(), hasEntry("name", "deny-topic-as-producer"));

        assertThat(producer.unbox(), hasKey("transactional"));
        var transactional = producer.unfold("transactional", String.class);
        assertThat(transactional.unbox(), hasEntry("id", "deny-transactional.id-as-producer"));

    }

    @Test
    void should_transform_create_to_deny_attributes_consumer_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // consumer
        assertThat(wrap.unbox(), hasKey("consumer"));
        var consumer = wrap.unfold("consumer");

        assertThat(consumer.unbox(), hasKey("topic"));
        var topic = consumer.unfold("topic", String.class);
        assertThat(topic.unbox(), hasEntry("name", "deny-topic-as-consumer"));

        assertThat(consumer.unbox(), hasKey("group"));
        var group = consumer.unfold("group", String.class);
        assertThat(group.unbox(), hasEntry("id", "deny-group.id-as-consumer"));

    }

    @Test
    void should_transform_create_to_deny_attributes_topic_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // topic
        assertThat(wrap.unbox(), hasKey("topic"));
        var topic = wrap.unfold("topic");

        assertThat(topic.unbox(), hasKey("name"));
        String name = topic.get("name");
        assertEquals("another-topic-name", name);

        assertThat(topic.unbox(), hasKey("operations"));
        List<String> operations = topic.get("operations");
        assertThat(operations, hasItems("Describe"));

    }

    @Test
    void should_transform_create_to_deny_attributes_group_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // group
        assertThat(wrap.unbox(), hasKey("group"));
        var group = wrap.unfold("group");

        assertThat(group.unbox(), hasKey("id"));
        String id = group.get("id");
        assertEquals("another-group.id", id);

        assertThat(group.unbox(), hasKey("operations"));
        List<String> operations = group.get("operations");
        assertThat(operations, hasItems("Read"));

    }

    @Test
    void should_transform_create_to_deny_attributes_transactional_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // transactional
        assertThat(wrap.unbox(), hasKey("transactional"));
        var transactional = wrap.unfold("transactional");

        assertThat(transactional.unbox(), hasKey("id"));
        String id = transactional.get("id");
        assertEquals("another-transactional.id", id);

        assertThat(transactional.unbox(), hasKey("operations"));
        List<String> operations = transactional.get("operations");
        assertThat(operations, hasItems("All"));

    }

    @Test
    void should_transform_create_to_deny_attributes_connection_backend_format() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = new MyMap<>(acl.toBackend().getAttributes());
        assertNotNull(actual);

        var wrap = actual.unfold("deny");

        // connection
        assertThat(wrap.unbox(), hasKey("connection"));
        var transactional = wrap.unfold("connection");

        assertThat(transactional.unbox(), hasKey("from"));
        List<String> from = transactional.get("from");
        assertThat(from, hasItems("192.168.0.100"));

    }

    @Test
    void should_serialize_original_create_file() throws IOException {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create.yaml");
        var json = Loader.loadAsJSONAndValidade(yaml);

        var acl = new ACLMigration(json, yaml);

        var actual = acl.toBackend().getOriginal();

        assertEquals(yaml.toString(), actual.getPath());
        assertNotNull(MigrationLoader.DEFAULT_CONTENT_TYPE, actual.getContentType());

        var expected = Base64.getEncoder().encodeToString(Files.readAllBytes(yaml));
        assertNotNull(expected, actual.getContent());
    }

    @Test
    void should_transform_patch_to_backend_format() {

        // TODO will be available in future release
    }

    @Test
    void should_transform_remove_to_backend_format() {

        // TODO remove yaml schema needs revision
    }
}
