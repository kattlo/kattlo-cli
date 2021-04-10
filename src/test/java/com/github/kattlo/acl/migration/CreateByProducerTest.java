package com.github.kattlo.acl.migration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;

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
public class CreateByProducerTest {

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

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_allow_producer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-allow.yaml");
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
        assertEquals("topic-allow-producer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-allow-producer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("topic-allow-producer", acl2.pattern().name());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_allow_idempotent_producer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-allow-idempotent.yaml");
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
            hasSize(4)
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
        assertEquals("topic-allow-producer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-allow-producer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("topic-allow-producer", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl3.entry().permissionType());
        assertEquals(AclOperation.IDEMPOTENT_WRITE, acl3.entry().operation());
        assertEquals("*", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.TOPIC, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("topic-allow-producer", acl3.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_allow_transactional_producer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-allow-transactional.yaml");
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
            hasSize(5)
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
        assertEquals("topic-allow-producer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-allow-producer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("topic-allow-producer", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl3.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl3.entry().operation());
        assertEquals("*", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("my-transactional.id", acl3.pattern().name());

        var acl4 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl4.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl4.entry().operation());
        assertEquals("*", acl4.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl4.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl4.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl4.pattern().patternType());
        assertEquals("my-transactional.id", acl4.pattern().name());
    }

    @Test
    public void should_throw_when_allow_producer_and_top_level_topic_has_same_value() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-allow-same-topic.yaml");
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
            containsString("producer topic and top level topic has the same name"));

    }

    @Test
    public void should_throw_when_allow_producer_and_top_level_transactional_has_same_value() {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-allow-same-transactional.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        //assert
        var actual = assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));

        assertThat(actual.getMessage(),
            containsString("producer transactional and top level on has the same id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_deny_producer() throws Exception {

        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-producer-deny.yaml");
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
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("topic-deny-producer", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-deny-producer", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("topic-deny-producer", acl2.pattern().name());
    }

}
