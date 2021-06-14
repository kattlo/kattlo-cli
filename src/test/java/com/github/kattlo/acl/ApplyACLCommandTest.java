package com.github.kattlo.acl;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.Matchers.*;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;

import com.github.kattlo.SharedOptionValues;
import com.github.kattlo.acl.migration.Strategy;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.kafka.Kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import picocli.CommandLine;

@QuarkusTest
public class ApplyACLCommandTest {

    @Inject
    CommandLine cli;

    @InjectMock
    Backend backend;

    @InjectMock
    Kafka kafka;

    private StringWriter out;
    private StringWriter err;

    private AdminClient admin;
    private Strategy strategy;

    private ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
    private MockedStatic<SharedOptionValues> mockedShared;

    private void mockitoWhen() throws Exception {

        when(backend.current(any(), anyString()))
            .thenReturn(Optional.empty());

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        mockedShared = mockStatic(SharedOptionValues.class);
        mockedShared.when(() -> SharedOptionValues.getKafkaConfiguration())
            .thenReturn(new Properties());

    }

    @BeforeEach
    public void beforeEach() {
        out = new StringWriter();
        err = new StringWriter();

        cli.setOut(new PrintWriter(out));
        cli.setErr(new PrintWriter(err));

        strategy = Mockito.mock(Strategy.class);
    }

    @AfterEach
    public void afterEach() {
        if(null!= mockedShared){
            mockedShared.close();
        }
    }

    @Test
    public void should_exit_code_2_when_directory_not_exists() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./_not_exists"
        };

        int actual = cli.execute(args);

        assertEquals(2, actual);
        assertThat(err.toString(), containsString(Path.of("./_not_exists").toString() + " not found or"));
    }

    @Test
    void should_exit_code_1_when_create_by_cluster_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/cluster/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_consumer_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/consumer/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_group_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/group/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_host_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/host/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_producer_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/producer/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_topic_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/topic/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_exit_code_1_when_create_by_transactional_does_not_follow_the_schema() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/transactional/01_does_not_follow_schema"
        };

        int actual = cli.execute(args);

        assertEquals(1, actual);
        assertThat(err.toString(), containsString("Does not follow the schema"));
    }

    @Test
    void should_execute_create_by_cluster() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/cluster/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.deny.cluster.operations[0]", equalTo("Alter")));
        }
    }

    @Test
    void should_execute_create_by_consumer() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/consumer/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.consumer.topic.name", equalTo("topic-as-consumer")));
            assertThat(json, hasJsonPath("$.create.allow.consumer.group.id", equalTo("group.id-as-consumer")));
        }
    }

    @Test
    void should_execute_create_by_group() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/group/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.group.id", equalTo("my-group.id")));
            assertThat(json, hasJsonPath("$.create.allow.group.operations[0]", equalTo("Read")));

            assertThat(json, hasJsonPath("$.create.deny.group.id", equalTo("my-group.id")));
            assertThat(json, hasJsonPath("$.create.deny.group.operations[0]", equalTo("Describe")));
        }
    }

    @Test
    void should_execute_create_by_host() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/host/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.connection.from[0]", equalTo("172.16.0.102")));
        }
    }

    @Test
    void should_execute_create_by_producer() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/producer/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.producer.topic.name", equalTo("topic-to-allow")));
            assertThat(json, hasJsonPath("$.create.allow.producer.transactional.id", equalTo("my-transactional.id")));
        }
    }

    @Test
    void should_execute_create_by_topic() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/topic/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.topic.name", equalTo("topic-just-allow")));
            assertThat(json, hasJsonPath("$.create.allow.topic.operations[0]", equalTo("Write")));
        }
    }

    @Test
    void should_execute_create_by_transactional() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/transactional/02_follow_schema"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();
            assertThat(json, hasJsonPath("$.create.allow.transactional.id", equalTo("my-transactional.id")));
            assertThat(json, hasJsonPath("$.create.allow.transactional.operations[0]", equalTo("Write")));
        }
    }

    @Test
    void should_execute_create_with_all_types_in_the_same_file() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/all/01_same_file"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(() -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.iterator().next().toString();

            assertThat(json, hasJsonPath("$.create.allow.producer"));
            assertThat(json, hasJsonPath("$.create.allow.consumer"));
            assertThat(json, hasJsonPath("$.create.allow.topic"));
            assertThat(json, hasJsonPath("$.create.allow.group"));
            assertThat(json, hasJsonPath("$.create.allow.cluster"));
            assertThat(json, hasJsonPath("$.create.allow.transactional"));
            assertThat(json, hasJsonPath("$.create.allow.connection"));

            assertThat(json, hasJsonPath("$.create.deny.producer"));
            assertThat(json, hasJsonPath("$.create.deny.consumer"));
            assertThat(json, hasJsonPath("$.create.deny.topic"));
            assertThat(json, hasJsonPath("$.create.deny.group"));
            assertThat(json, hasJsonPath("$.create.deny.transactional"));
            assertThat(json, hasJsonPath("$.create.deny.connection"));
        }
    }

    @Test
    void should_execute_create_with_all_types_separated_files() throws Exception {

        // setup
        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/all/02_separated_files"
        };

        mockitoWhen();

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(Mockito.times(7), () -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(7, actual.size());

            var json = actual.get(0).toString();
            assertThat(json, hasJsonPath("$.create.deny.cluster"));

            json = actual.get(1).toString();
            assertThat(json, hasJsonPath("$.create.allow.consumer"));

            json = actual.get(2).toString();
            assertThat(json, hasJsonPath("$.create.allow.group"));
            assertThat(json, hasJsonPath("$.create.deny.group"));

            json = actual.get(3).toString();
            assertThat(json, hasJsonPath("$.create.allow.connection"));

            json = actual.get(4).toString();
            assertThat(json, hasJsonPath("$.create.allow.producer"));

            json = actual.get(5).toString();
            assertThat(json, hasJsonPath("$.create.allow.topic"));

            json = actual.get(6).toString();
            assertThat(json, hasJsonPath("$.create.allow.transactional"));
        }
    }

    @Test
    void should_execute_create_just_the_newest_migration_strategy() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/all/02_separated_files"
        };

        final var applied = new Resource();
        applied.setVersion("v0006");

        when(backend.current(any(), anyString()))
            .thenReturn(Optional.of(applied));

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        mockedShared = mockStatic(SharedOptionValues.class);
        mockedShared.when(() -> SharedOptionValues.getKafkaConfiguration())
            .thenReturn(new Properties());

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

            mocked.verify(Mockito.times(1), () -> Strategy.of(jsonCaptor.capture()));
            var actual = jsonCaptor.getAllValues();

            // assert
            assertEquals(1, actual.size());

            var json = actual.get(0).toString();
            assertThat(json, hasJsonPath("$.create.allow.transactional"));
        }
    }

    @Test
    void should_exit_code_0_when_there_is_not_new_migrations() {

        String[] args = {
            "--config-file=./src/test/resources/.kattlo.yaml",
            "--kafka-config-file=./src/test/resources/kafka.properties",
            "apply",
            "acl",
            "--directory=./src/test/resources/acl/by-principal/all/02_separated_files"
        };

        final var applied = new Resource();
        applied.setVersion("v0007");

        when(backend.current(any(), anyString()))
            .thenReturn(Optional.of(applied));

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        mockedShared = mockStatic(SharedOptionValues.class);
        mockedShared.when(() -> SharedOptionValues.getKafkaConfiguration())
            .thenReturn(new Properties());

        try(var mocked = mockStatic(Strategy.class)){
            mocked.when(() -> Strategy.of(any()))
                .thenReturn(strategy);

            // act
            var exitno = cli.execute(args);
            assertEquals(0, exitno);

        }
    }
}
