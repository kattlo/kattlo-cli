package com.github.kattlo.acl.migration;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;

import com.github.kattlo.acl.yaml.Loader;
import com.github.kattlo.core.yaml.MigrationLoader;

import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import testsupport.AclCreateTestBase;

@ExtendWith(MockitoExtension.class)
public class CreateByTransactionalTest extends AclCreateTestBase {

    @Test
    public void should_throw_when_invalid_allow_operation() {
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-invalid-allow.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));
    }

    @Test
    public void should_throw_when_invalid_deny_operation() {
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-invalid-deny.yaml");
        var map = Loader.loadAsMap(yaml);

        var migration = MigrationLoader.parseJson(map);

        var strategy = Strategy.of(migration);

        // assert
        assertThrows(AclCreateException.class, () ->
            // act
            strategy.execute(admin));
    }

    @Test
    public void should_throw_when_allow_and_deny_has_the_same_operation() {
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-same-operation-allow-deny.yaml");
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
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-same-ip-allow-deny.yaml");
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
    public void should_create_just_with_allow_operation() throws Exception{
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-just-allow.yaml");
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
            hasSize(1)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl0.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("172.16.0.100", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-transactional.id", acl0.pattern().name());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_just_with_deny_operation() throws Exception{
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-just-deny.yaml");
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
            hasSize(1)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.DENY, acl0.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("172.16.0.100", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-transactional.id", acl0.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_with_allow_and_deny_operation() throws Exception {
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-allow-and-deny.yaml");
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
        assertEquals("172.16.0.100", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("my-transactional.id", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl1.entry().operation());
        assertEquals("192.168.0.100", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("my-transactional.id", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.DESCRIBE, acl2.entry().operation());
        assertEquals("172.16.0.150", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("my-transactional.id", acl2.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_with_prefixed_id() throws Exception {
        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-transactional-prefixed-name.yaml");
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
            hasSize(1)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl0.entry().permissionType());
        assertEquals(AclOperation.WRITE, acl0.entry().operation());
        assertEquals("172.16.0.100", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl0.pattern().resourceType());
        assertEquals(PatternType.PREFIXED, acl0.pattern().patternType());
        assertEquals("my-transactional-", acl0.pattern().name());
    }

    @Test
    public void should_throw_when_does_not_exist() {
    }
}
