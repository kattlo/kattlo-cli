package com.github.kattlo.core.report;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.core.StringContains.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import javax.json.bind.JsonbBuilder;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Original;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceStatus;
import com.github.kattlo.core.backend.ResourceType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintStreamReporterTest {

    /*
    + create TOPIC
      v0001
        original -> /path/to/migration.yaml
        resource -> topic-name
        att1 -> vlu1
        att1 -> vlu2
    */

    private ByteArrayOutputStream buffer;
    private PrintStream out;
    private PrintStreamReporter reporter;

    @BeforeEach
    public void beforeEach(){

        buffer = new ByteArrayOutputStream();
        out = new PrintStream(buffer);
        reporter = new PrintStreamReporter(out);

    }

    @Test
    public void should_report_a_create_migration() {

        var expected = "+ create TOPIC";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));

    }

    @Test
    public void should_report_the_version() {

        var expected = "  v0001";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_the_resource_name() {

        var expected = "  resource -> topic-name";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_the_original() {

        var expected = "  original -> /path/to/migration.yaml";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_the_attributes() {

        var expected = "  partitions -> 1";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        var config = Map.of();
        var attributes = Map.of(
            "partitions", "1",
            "replicationFactor", "1",
            "config", config
        );
        create.setAttributes(attributes);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));

    }

    @Test
    public void should_report_a_patch_migration() {

        var expected = "~ patch TOPIC";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.PATCH);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_a_remove_migration() {

        var expected = "! remove TOPIC";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.REMOVE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_a_import() {

        var expected = "= import TOPIC";

        var create = new Migration();
        create.setVersion("v0001");
        create.setOperation(OperationType.CREATE);
        create.setNotes("notes");
        create.setResourceType(ResourceType.TOPIC);
        create.setResourceName("topic-name");
        create.setTimestamp(LocalDateTime.now());

        var original = new Original();
        original.setContent("Y29udGVudA==");
        original.setContentType("text/yaml");
        original.setPath("/path/to/migration.yaml");
        create.setOriginal(original);

        reporter.report(create, Boolean.TRUE);

        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_then_plain_format_current() {

        var expected = "AVAILABLE\nTOPIC : topic-name-0\nversion: v0002";
        var format = ReportFormat.PLAIN;

        var topic = "topic-name-0";

        var resource = new Resource();
        resource.setVersion("v0002");
        resource.setStatus(ResourceStatus.AVAILABLE);
        resource.setResourceType(ResourceType.TOPIC);
        resource.setResourceName(topic);
        resource.setTimestamp(LocalDateTime.now());
        resource.setAttributes(Map.of(
            "partitions", "7",
            "config", Map.of(
                "compression.type", "snappy"
            )
        ));

        reporter.current(resource, format);
        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));

    }

    @Test
    public void should_report_then_plain_format_current_with_attributes() {

        var expected = "  config -> {compression.type=snappy}\n  partitions -> 7\n  replicationFactor -> 2";

        var format = ReportFormat.PLAIN;

        var topic = "topic-name-0";

        var resource = new Resource();
        resource.setVersion("v0002");
        resource.setStatus(ResourceStatus.AVAILABLE);
        resource.setResourceType(ResourceType.TOPIC);
        resource.setResourceName(topic);
        resource.setTimestamp(LocalDateTime.now());
        resource.setAttributes(Map.of(
            "partitions", "7",
            "replicationFactor", "2",
            "config", Map.of(
                "compression.type", "snappy"
            )
        ));

        reporter.current(resource, format);
        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_then_json_format_of_current() {

        var format = ReportFormat.JSON;

        var topic = "topic-name-0";

        var resource = new Resource();
        resource.setVersion("v0002");
        resource.setStatus(ResourceStatus.AVAILABLE);
        resource.setResourceType(ResourceType.TOPIC);
        resource.setResourceName(topic);
        resource.setTimestamp(LocalDateTime.now());
        resource.setAttributes(Map.of(
            "partitions", "7",
            "config", Map.of(
                "compression.type", "snappy"
            )
        ));

        reporter.current(resource, format);
        var actualJson = buffer.toString();

        // assert
        var actual = JsonbBuilder.create()
            .fromJson(actualJson, Resource.class);

        assertEquals(resource, actual);
    }

    @Test
    public void should_report_the_plain_format_of_history() {

        // setup
        var expected = "TOPIC: topic-name-0\n\n  v0002 -> PATCH";
        var topic = "topic-name-0";
        var format = ReportFormat.PLAIN;

        var migration1 = new Migration();
        migration1.setVersion("v0001");
        migration1.setOperation(OperationType.CREATE);
        migration1.setNotes("Some notes about v0001");
        migration1.setResourceType(ResourceType.TOPIC);
        migration1.setResourceName(topic);
        migration1.setTimestamp(LocalDateTime.now());
        migration1.setAttributes(Map.of(
            "partitions", "11",
            "replicationFactor", "2",
            "config", Map.of(
                "retention.ms", "-1"
            )
        ));
        migration1.setOriginal(new Original(
            "/v0001_create-topic.yml", "text/yaml", "content"));

        var migration2 = new Migration();
        migration2.setVersion("v0002");
        migration2.setOperation(OperationType.PATCH);
        migration2.setNotes("Some notes about v0002");
        migration2.setResourceType(ResourceType.TOPIC);
        migration2.setResourceName(topic);
        migration2.setTimestamp(LocalDateTime.now());
        migration2.setAttributes(Map.of(
            "config", Map.of(
                "min.in.sync.replicas", "2"
            )
        ));
        migration2.setOriginal(new Original(
            "/v0002_patch.yml", "text/yaml", "content"));

        // act
        reporter.history(Stream.of(migration1, migration2), format);
        var actual = buffer.toString();

        // assert
        assertThat(actual, containsString(expected));
    }

    @Test
    public void should_report_the_json_format_of_history() {

        // setup
        var topic = "topic-name-0";
        var format = ReportFormat.JSON;

        var migration1 = new Migration();
        migration1.setVersion("v0001");
        migration1.setOperation(OperationType.CREATE);
        migration1.setNotes("Some notes about v0001");
        migration1.setResourceType(ResourceType.TOPIC);
        migration1.setResourceName(topic);
        migration1.setTimestamp(LocalDateTime.now());
        migration1.setAttributes(Map.of(
            "partitions", "11",
            "replicationFactor", "2",
            "config", Map.of(
                "retention.ms", "-1"
            )
        ));
        migration1.setOriginal(new Original(
            "/v0001_create-topic.yml", "text/yaml", "content"));

        var migration2 = new Migration();
        migration2.setVersion("v0002");
        migration2.setOperation(OperationType.PATCH);
        migration2.setNotes("Some notes about v0002");
        migration2.setResourceType(ResourceType.TOPIC);
        migration2.setResourceName(topic);
        migration2.setTimestamp(LocalDateTime.now());
        migration2.setAttributes(Map.of(
            "config", Map.of(
                "min.in.sync.replicas", "2"
            )
        ));
        migration2.setOriginal(new Original(
            "/v0002_patch.yml", "text/yaml", "content"));

        // act
        reporter.history(Stream.of(migration1, migration2), format);
        var actualJson = buffer.toString();

        // assert
        var actual = JsonbBuilder.create().fromJson(actualJson, Map.class);

        assertEquals(topic, actual.get("TOPIC"));
        assertNotNull(actual.get("history"));
    }

    @Test
    public void should_report_a_generated_resource() {

        /*
            Generated at: /path/to/file
        */
        var expected = "\nGenerated at: /path/to/file\n";

        reporter.generated(Path.of("/path/to/file"));

        var actual = buffer.toString();

        assertThat(actual, containsString(expected));
    }
}
