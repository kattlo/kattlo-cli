package com.github.kattlo.acl.migration;

import static com.github.kattlo.acl.migration.CreateStrategy.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.kattlo.util.JSONPointer;
import com.github.kattlo.util.JSONUtil;
import com.github.kattlo.util.StringUtil;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.ResourceType;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class CreateByConsumer implements Strategy {

    static final String THROW_MSG_TOPIC = "consumer topic and top level topic has the same name %s";
    static final String RELATIVE_POINTER = "#/consumer";
    static final String GROUP_RELATIVE_POINTER = "#/group";

    private final JSONObject migration;
    CreateByConsumer(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    private List<AclBinding> bindingsFor(String principal, JSONObject access,
        AclPermissionType permission) {

        var consumer = JSONPointer.asObject(access, RELATIVE_POINTER).get();
        var topic = JSONPointer.asObject(consumer, CreateByTopic.RELATIVE_POINTER)
            .map(t -> t.getString(CreateByTopic.NAME_ATTRIBUTE))
            .get();
        var group = JSONPointer.asObject(consumer, GROUP_RELATIVE_POINTER)
            .map(g -> g.getString("id"))
            .get();

        log.debug("Consumer topic {}", topic);
        log.debug("Consumer group {}", group);

        var topicPattern = patternOf(topic, ResourceType.TOPIC);
        log.debug("Topic ResourcePattern {}", topicPattern);

        var groupPattern = patternOf(group, ResourceType.GROUP);
        log.debug("Group ResourcePattern {}", groupPattern);

        var ips = JSONPointer.asArray(access, CONNECTION_RELATIVE_POINTER)
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(WILDCARD_ALL));
        log.debug("List of IPs to {}: {}", permission, ips);

        var readTopic = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.READ, permission))
            .map(a -> new AclBinding(topicPattern, a));

        var describeTopic = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.DESCRIBE, permission))
            .map(a -> new AclBinding(topicPattern, a));

        var readGroup = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.READ, permission))
            .map(a -> new AclBinding(groupPattern, a));

        var result = Stream.concat(Stream.concat(readTopic, describeTopic), readGroup)
            .collect(Collectors.toList());
        log.debug("By Consumer result {}", result);

        return result;
    }

    private void scanForRepeatedGroup(Optional<JSONObject> operation) {

        var group = operation
            .flatMap(o -> JSONPointer.asObject(o, RELATIVE_POINTER))
            .flatMap(c -> JSONPointer.asObject(c, GROUP_RELATIVE_POINTER))
            .map(g -> g.getString("id"));

        var toplevelGroup = operation
            .flatMap(o -> JSONPointer.asObject(o, GROUP_RELATIVE_POINTER))
            .map(g -> g.getString("id"))
            .orElseGet(() -> StringUtil.NO_VALUE);

        group.filter(g -> g.equals(toplevelGroup))
            .ifPresent(g -> {
                throw new AclCreateException("consumer group and top level one has the same id: " + g);
            });
    }

    @Override
    public void execute(AdminClient admin) {

        var principal = JSONPointer.asString(migration,
            PRINCIPAL_ABSOLUTE_POINTER).get();

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        scanForRepeatedTopic(allow, RELATIVE_POINTER).ifPresent(t -> {
            throw new AclCreateException("allow consumer topic and top level one has the same name: " + t);
        });
        scanForRepeatedTopic(deny, RELATIVE_POINTER).ifPresent(t -> {
            throw new AclCreateException("deny consumer topic and top level one has the same name: " + t);
        });

        scanForRepeatedGroup(allow);
        scanForRepeatedGroup(deny);

        var ipsToAllow = connectionIPs(allow);
        var ipsToDeny = connectionIPs(deny);

        scanForRepeatedIP(ipsToAllow, ipsToDeny);

        // create bindings
        var toAllow = allow
            .map(a -> bindingsFor(principal, a, AclPermissionType.ALLOW))
            .orElseGet(() -> List.of());

        var toDeny = deny
            .map(a -> bindingsFor(principal, a, AclPermissionType.DENY))
            .orElseGet(() -> List.of());

        // apply
        apply(toAllow, toDeny, admin);
    }

}
