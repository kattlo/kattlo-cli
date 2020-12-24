package com.github.kattlo.core.report;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Map;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.Original;
import com.github.kattlo.core.backend.ResourceType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintStreamReporterTest {

    /*
    + create a TOPIC
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

        var expected = "+ create";

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

        var expected = "~ patch";

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

        var expected = "! remove";

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

}
