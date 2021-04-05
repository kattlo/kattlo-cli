package com.github.kattlo.acl.migration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

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
public class CreateByTopicTest {

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
    public void should_throw_when_invalid_topic_allow_operation() throws Exception{

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-invalid-allow.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));

    }

    @Test
    public void should_throw_when_invalid_topic_deny_operation() {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-invalid-deny.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));
    }

    @Test
    public void should_throw_when_topic_allow_and_deny_has_the_same_operation() {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-same-operation-allow-deny.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));
    }

    @Test
    public void should_throw_when_allow_and_deny_host_has_the_same_ip() {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-same-ip-allow-deny.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_just_with_allow_operation() throws Exception{

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-just-allow.yaml");
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
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("topic-just-allow", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.READ, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-just-allow", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("topic-just-allow", acl2.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_just_with_deny_operation() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-just-deny.yaml");
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
            hasSize(2)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.DENY, acl0.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("topic-just-deny", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("topic-just-deny", acl1.pattern().name());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_create_topic_with_allow_and_deny_operation() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-without-connection-from.yaml");
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
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-topic-name", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.ALTER, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("my-topic-name", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.READ, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("my-topic-name", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.DENY, acl3.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl3.entry().operation());
        assertEquals("*", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.TOPIC, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("my-topic-name", acl3.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_list_of_allow_ip() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-allow-two-ip.yaml");
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
        assertEquals("192.168.0.20", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-topic-name", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.READ, acl1.entry().operation());
        assertEquals("192.168.0.22", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("my-topic-name", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl2.entry().operation());
        assertEquals("192.168.0.20", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("my-topic-name", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl3.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl3.entry().operation());
        assertEquals("192.168.0.22", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.TOPIC, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("my-topic-name", acl3.pattern().name());

    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_create_topic_with_list_of_deny_ip() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-deny-two-ip.yaml");
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
        assertEquals(AclPermissionType.DENY, acl0.entry().permissionType());
        assertEquals(AclOperation.READ, acl0.entry().operation());
        assertEquals("192.168.0.20", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-topic-name", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.READ, acl1.entry().operation());
        assertEquals("192.168.0.22", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("my-topic-name", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl2.entry().operation());
        assertEquals("192.168.0.20", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("my-topic-name", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.DENY, acl3.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl3.entry().operation());
        assertEquals("192.168.0.22", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.TOPIC, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("my-topic-name", acl3.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_allow_and_deny_host() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-allow-and-deny.yaml");
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
        assertEquals(AclOperation.DESCRIBE, acl0.entry().operation());
        assertEquals("192.168.0.22", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-topic-name", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.READ, acl1.entry().operation());
        assertEquals("192.168.0.20", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("my-topic-name", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl2.entry().operation());
        assertEquals("192.168.0.20", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TOPIC, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("my-topic-name", acl2.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_topic_with_prefixed_name() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-topic-prefixed-name.yaml");
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
            hasSize(2)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl0.entry().permissionType());
        assertEquals(AclOperation.READ, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.PREFIXED, acl0.pattern().patternType());
        assertEquals("payments-", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TOPIC, acl1.pattern().resourceType());
        assertEquals(PatternType.PREFIXED, acl1.pattern().patternType());
        assertEquals("payments-", acl1.pattern().name());
    }

    @Test
    public void should_throw_when_topic_does_not_exists() {

        // TODO Throw when topic its not managed by kattlo?
    }
}
