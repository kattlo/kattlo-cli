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
public class CreateByHostTest extends AclCreateTestBase {

    @Test
    void should_throw_when_allow_and_deny_host_has_the_same_ip() {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-ip-allow-deny-same-ip.yaml");
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
    void should_create_just_with_allow_operation() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-ip-allow.yaml");
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
        assertEquals(AclOperation.ALL, acl0.entry().operation());
        assertEquals("172.16.0.102", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("*", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.ALL, acl1.entry().operation());
        assertEquals("172.16.0.102", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.GROUP, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("*", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.ALL, acl2.entry().operation());
        assertEquals("172.16.0.102", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("*", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl3.entry().permissionType());
        assertEquals(AclOperation.ALL, acl3.entry().operation());
        assertEquals("172.16.0.102", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.CLUSTER, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("*", acl3.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    void should_create_just_with_deny_operation() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-ip-deny.yaml");
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
        assertEquals(AclOperation.ALL, acl0.entry().operation());
        assertEquals("172.16.0.102", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("*", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.ALL, acl1.entry().operation());
        assertEquals("172.16.0.102", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.GROUP, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("*", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.ALL, acl2.entry().operation());
        assertEquals("172.16.0.102", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("*", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.DENY, acl3.entry().permissionType());
        assertEquals(AclOperation.ALL, acl3.entry().operation());
        assertEquals("172.16.0.102", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.CLUSTER, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("*", acl3.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    void should_create_with_allow_and_deny() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-ip-allow-deny.yaml");
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
            hasSize(8)
        ));

        var acls = actual.iterator();
        var acl0 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl0.entry().permissionType());
        assertEquals(AclOperation.ALL, acl0.entry().operation());
        assertEquals("172.16.0.102", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("*", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl1.entry().permissionType());
        assertEquals(AclOperation.ALL, acl1.entry().operation());
        assertEquals("172.16.0.102", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.GROUP, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("*", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl2.entry().permissionType());
        assertEquals(AclOperation.ALL, acl2.entry().operation());
        assertEquals("172.16.0.102", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("*", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.ALLOW, acl3.entry().permissionType());
        assertEquals(AclOperation.ALL, acl3.entry().operation());
        assertEquals("172.16.0.102", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.CLUSTER, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("*", acl3.pattern().name());

        var acl4 = acls.next();
        assertEquals(AclPermissionType.DENY, acl4.entry().permissionType());
        assertEquals(AclOperation.ALL, acl4.entry().operation());
        assertEquals("192.168.0.100", acl4.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl4.entry().principal());
        assertEquals(ResourceType.TOPIC, acl4.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl4.pattern().patternType());
        assertEquals("*", acl4.pattern().name());

        var acl5 = acls.next();
        assertEquals(AclPermissionType.DENY, acl5.entry().permissionType());
        assertEquals(AclOperation.ALL, acl5.entry().operation());
        assertEquals("192.168.0.100", acl5.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl5.entry().principal());
        assertEquals(ResourceType.GROUP, acl5.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl5.pattern().patternType());
        assertEquals("*", acl5.pattern().name());

        var acl6 = acls.next();
        assertEquals(AclPermissionType.DENY, acl6.entry().permissionType());
        assertEquals(AclOperation.ALL, acl6.entry().operation());
        assertEquals("192.168.0.100", acl6.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl6.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl6.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl6.pattern().patternType());
        assertEquals("*", acl6.pattern().name());

        var acl7 = acls.next();
        assertEquals(AclPermissionType.DENY, acl7.entry().permissionType());
        assertEquals(AclOperation.ALL, acl7.entry().operation());
        assertEquals("192.168.0.100", acl7.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl7.entry().principal());
        assertEquals(ResourceType.CLUSTER, acl7.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl7.pattern().patternType());
        assertEquals("*", acl7.pattern().name());
    }

    @SuppressWarnings("unchecked")
    @Test
    void should_create_with_all_wildcard_ip() throws Exception {

        // setup
        var yaml = Path.of("./src/test/resources/acl/by-principal/v0001_create-ip-deny-all.yaml");
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
        assertEquals(AclOperation.ALL, acl0.entry().operation());
        assertEquals("*", acl0.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl0.entry().principal());
        assertEquals(ResourceType.TOPIC, acl0.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl0.pattern().patternType());
        assertEquals("*", acl0.pattern().name());

        var acl1 = acls.next();
        assertEquals(AclPermissionType.DENY, acl1.entry().permissionType());
        assertEquals(AclOperation.ALL, acl1.entry().operation());
        assertEquals("*", acl1.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl1.entry().principal());
        assertEquals(ResourceType.GROUP, acl1.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl1.pattern().patternType());
        assertEquals("*", acl1.pattern().name());

        var acl2 = acls.next();
        assertEquals(AclPermissionType.DENY, acl2.entry().permissionType());
        assertEquals(AclOperation.ALL, acl2.entry().operation());
        assertEquals("*", acl2.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl2.entry().principal());
        assertEquals(ResourceType.TRANSACTIONAL_ID, acl2.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl2.pattern().patternType());
        assertEquals("*", acl2.pattern().name());

        var acl3 = acls.next();
        assertEquals(AclPermissionType.DENY, acl3.entry().permissionType());
        assertEquals(AclOperation.ALL, acl3.entry().operation());
        assertEquals("*", acl3.entry().host());
        assertEquals("User:CN=Alice,OU=Sales,O=Unknown,L=Unknown,ST=SP,C=Unknown",
            acl3.entry().principal());
        assertEquals(ResourceType.CLUSTER, acl3.pattern().resourceType());
        assertEquals(PatternType.LITERAL, acl3.pattern().patternType());
        assertEquals("*", acl3.pattern().name());
    }

}
