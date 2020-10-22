package com.github.kattlo.topic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Collection;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author fabiojose
 */
@ExtendWith(MockitoExtension.class)
public class CreateStrategyTest {

    @Mock
    AdminClient admin;

    @Mock
    CreateTopicsResult result;

    @Mock
    KafkaFuture<Void> future;

    @Captor
    private ArgumentCaptor<Collection<NewTopic>> newTopicCaptor;

    private void mockitoWhen() throws Exception {

        when(admin.createTopics(anyCollection()))
            .thenReturn(result);

        when(result.all())
            .thenReturn(future);

        when(future.get())
            .thenReturn((Void)null);

    }

    @Test
    public void should_create_with_partitions_and_replication_factor()
        throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0001")
            .operation("create")
            .notes("notes")
            .topic("topic")
            .partitions(3)
            .replicationFactor(3)
            .config(null)
            .build();

        var create = Strategy.of(operation);

        mockitoWhen();

        // act
        create.execute(admin);

        verify(admin).createTopics(newTopicCaptor.capture());
        var actual = newTopicCaptor.getValue();

        // assert
        assertEquals(1, actual.size());

        var newTopic = actual.iterator().next();
        assertEquals(operation.getTopic(), newTopic.name());
        assertEquals(operation.getPartitions(), newTopic.numPartitions());
        assertEquals(operation.getReplicationFactor(), newTopic.replicationFactor());
    }

    @Test
    public void should_create_with_default_partitions() {

    }

    @Test
    public void should_create_with_default_replication_factor() {

    }

    @Test
    public void should_create_with_default_partitions_replication_factor_and_config() {

    }

    @Test
    public void should_throw_when_fail_to_create() {

    }
}
