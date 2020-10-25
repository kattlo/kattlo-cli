package com.github.kattlo.topic.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
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
    AlterConfigsResult configsResult;

    @Mock
    KafkaFuture<Void> partitionsResultFuture;

    @Mock
    KafkaFuture<Void> configsResultFuture;

    @Captor
    ArgumentCaptor<Map<String, NewPartitions>> newPartitionsCaptor;

    @Captor
    ArgumentCaptor<Map<ConfigResource, Collection<AlterConfigOp>>> newConfigCaptor;

    private void partitionsMockitoWhen() throws Exception {

        when(admin.createPartitions(anyMap()))
            .thenReturn(partitionsResult);

        when(partitionsResult.all())
            .thenReturn(partitionsResultFuture);

        when(partitionsResultFuture.get())
            .thenReturn((Void)null);

    }

    private void configMockitoWhen() throws Exception {

        when(admin.incrementalAlterConfigs(anyMap()))
            .thenReturn(configsResult);

        when(configsResult.all())
            .thenReturn(configsResultFuture);

        when(configsResultFuture.get())
            .thenReturn((Void)null);
    }

    @Test
    public void should_patch_partitions() throws Exception {

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
        //TODO
    }

    @Test
    public void should_patch_config() throws Exception {

        // setup
        var config = Map.of("compression.type", (Object)"snappy");

        var operation = TopicOperation.builder()
            .file(Path.of("first"))
            .version("v0002")
            .operation("patch")
            .notes("notes")
            .topic("topic")
            .config(config)
            .build();

        var patch = Strategy.of(operation);

        configMockitoWhen();

        // act
        patch.execute(admin);

        verify(admin).incrementalAlterConfigs(newConfigCaptor.capture());
        var captured = newConfigCaptor.getValue();

        var resource = new ConfigResource(Type.TOPIC, operation.getTopic());
        var actual = captured.get(resource);

        //assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(
            config.get("compression.type"),
            actual.iterator().next().configEntry().value());
    }

    @Test
    public void should_patch_config_to_cluster_default() {

    }

    @Test
    public void should_fail_when_exception_to_patch() {

    }
}
