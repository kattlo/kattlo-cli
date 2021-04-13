package com.github.kattlo.acl.migration;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Collection;

import com.github.kattlo.acl.yaml.Loader;
import com.github.kattlo.core.yaml.MigrationLoader;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateByConsumerTest {

    @Mock
    AdminClient admin;

    @Mock
    CreateAclsResult result;

    @Mock
    KafkaFuture<Void> future;

    @Captor
    private ArgumentCaptor<Collection<AclBinding>> newACLCaptor;

    private void mockitoWhen() throws Exception {

        when(admin.createAcls(anyCollection()))
            .thenReturn(result);

        when(result.all())
            .thenReturn(future);

        when(future.get())
            .thenReturn((Void)null);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_create_topic_with_allow_consumer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-consumer-allow.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        mockitoWhen();

        // act
        strategy.execute(admin);

        // assert
        verify(admin).createAcls(newACLCaptor.capture());
        var actual = newACLCaptor.getValue();

        assertThat(actual, allOf(
            hasSize(3)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl0.entry().permissionType());
        assertEquals(AclOperation.READ, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("topic-as-consumer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-as-consumer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.READ, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.GROUP, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("group.id-as-consumer", acl2.pattern().name());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_deny_consumer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-consumer-deny.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        mockitoWhen();

        // act
        strategy.execute(admin);

        // assert
        verify(admin).createAcls(newACLCaptor.capture());
        var actual = newACLCaptor.getValue();

        assertThat(actual, allOf(
            hasSize(3)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.DENY, acl0.entry().permissionType());
        assertEquals(AclOperation.READ, acl0.entry().operation());
        assertEquals("172.16.0.1", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("topic-as-consumer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl1.entry().operation());
        assertEquals("172.16.0.1", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-as-consumer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.READ, acl2.entry().operation());
        assertEquals("172.16.0.1", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.GROUP, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("group.id-as-consumer", acl2.pattern().name());
    }

    @Test
    public void should_throw_when_allow_consumer_and_top_level_topic_has_same_value() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-consumer-allow-same-topic.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // for create by topic strategy
        mockitoWhen();

        //assert
        var actual = assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));

        assertThat(actual.getMessage(),
            containsString("consumer topic and top level one has the same name"));
    }

    @Test
    public void should_throw_when_allow_consumer_and_top_level_group_has_same_value() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-consumer-allow-same-group.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        //assert
        var actual = assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));

        assertThat(actual.getMessage(),
            containsString("consumer group and top level one has the same id"));
    }
}
