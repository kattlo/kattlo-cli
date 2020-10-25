package com.github.kattlo.topic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Map;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.NewPartitions;
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
public class PatchStategyTest {

    @Mock
    AdminClient admin;

    @Mock
    CreatePartitionsResult partitionsResult;

    @Mock
    KafkaFuture<Void> future;

    @Captor
    ArgumentCaptor<Map<String, NewPartitions>> newPartitionsCaptor;

    private void partitionsMockitoWhen() throws Exception {

        when(admin.createPartitions(anyMap()))
            .thenReturn(partitionsResult);

        when(partitionsResult.all())
            .thenReturn(future);

        when(future.get())
            .thenReturn((Void)null);

    }

    @Test
    public void should_patch_patitions() throws Exception {

        // setup
        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .partitions(3)
            .build();

        var patch = Strategy.of(operation);

        partitionsMockitoWhen();

        // act
        patch.execute(admin);

        verify(admin).createPartitions(newPartitionsCaptor.capture());
        var captured = newPartitionsCaptor.getValue();

        var actual = captured.get(operation.getTopic());

        //assert
        assertNotNull(actual);
        assertEquals(operation.getPartitions(), actual.totalCount());
    }

    @Test
    public void should_patch_replication_factor() {

    }

    @Test
    public void should_patch_config() {

    }

    @Test
    public void should_patch_config_to_cluster_default() {

    }

    @Test
    public void should_fail_when_exception_to_patch() {

    }
}
