package com.github.kattlo.acl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import com.github.kattlo.EntryCommand;
import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.kafka.Kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

@ExtendWith(MockitoExtension.class)
public class ApplyACLCommandTest {

    private EntryCommand entry = new EntryCommand();
    private StringWriter out;
    private StringWriter err;
    private CommandLine cli;

    @Mock
    private EntryCommand parent;

    @Mock
    private CommandSpec spec;

    @Mock
    private Backend backend;

    @Mock
    private Kafka kafka;

    @Mock
    private AdminClient admin;

    private void mockitoWhen() throws Exception {

        when(backend.current(any(), anyString()))
            .thenReturn(Optional.empty());

        when(kafka.adminFor(any()))
            .thenReturn(admin);

        when(parent.getConfiguration())
            .thenReturn(new File("./src/test/resources/topics/rules/.kattlo_empty.yaml"));

    }

    @BeforeEach
    public void beforeEach() {
        out = new StringWriter();
        err = new StringWriter();

        cli = new CommandLine(entry);
        cli.setOut(new PrintWriter(out));
        cli.setErr(new PrintWriter(err));
    }

    @Test
    void should_exit_code_2_when_create_by_cluster_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_consumer_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_group_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_host_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_producer_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_topic_does_not_follow_the_schema() {

    }

    @Test
    void should_exit_code_2_when_create_by_transacional_does_not_follow_the_schema() {

    }

    @Test
    void should_execute_create_by_cluster() {

    }

    @Test
    void should_execute_create_by_consumer() {

    }

    @Test
    void should_execute_create_by_group() {

    }

    @Test
    void should_execute_create_by_host() {

    }

    @Test
    void should_execute_create_by_producer() {

    }

    @Test
    void should_execute_create_by_topic() {

    }

    @Test
    void should_execute_create_by_transactional() {

    }

    @Test
    void should_execute_create_with_all_types_in_the_same_file() {

    }

    @Test
    void should_exit_code_2_when_create_with_all_types_in_the_same_does_not_follow_the_schema() {

    }

    @Test
    void should_execute_create_with_all_types_separated_files() {

    }

    @Test
    void should_execute_create_just_the_newest_migration_strategy() {

    }
}
