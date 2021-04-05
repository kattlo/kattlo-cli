package com.github.kattlo.acl.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.github.kattlo.util.JSONPointer;
import com.github.kattlo.util.JSONUtil;

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
public class CreateByTopic implements Strategy {

    private static final String WILDCARD_ALL = "*";

    static final String RELATIVE_POINTER = "#/topic";

    private final JSONObject migration;
    CreateByTopic(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    private List<AclBinding> topicBindingsFor(String principal, JSONObject access,
            AclPermissionType permission){

        var topic = JSONPointer.asObject(access, RELATIVE_POINTER).get();
        var topicName = topic.getString("name");

        var operationsJson = JSONPointer.asArray(topic, "#/operations");
        log.info("Operations to {}: {}", permission, operationsJson);

        var pattern = CreateStrategy.patternOf(topicName, ResourceType.TOPIC);
        log.info("Topic ResourcePattern {}", pattern);

        List<AclOperation> operations;
        try {
            operations = CreateStrategy.parseOperation(operationsJson);
            log.info("Parsed operations to {}: {}", permission, operations);

        }catch(IllegalArgumentException e){
            throw new AclCreateException("Some accesses to " + permission + " are invalid", e);
        }

        var ips = JSONPointer.asArray(access, "#/connection/from")
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(WILDCARD_ALL));
        log.info("List of IPs to {}: {}", permission, ips);

        return operations.stream()
            .flatMap(operation ->
                ips.stream()
                    .map(ip -> new AccessControlEntry(principal, ip, operation, permission))
            )
            .map(entry -> new AclBinding(pattern, entry))
            .collect(Collectors.toList());

    }

    @Override
    public void execute(AdminClient admin) {

        var principal = JSONPointer.asString(migration, CreateStrategy.PRINCIPAL_ABSOLUTE_POINTER).get();
        log.debug("Creating ACL for Principal {}", principal);

        var allow = JSONPointer.asObject(migration, CreateStrategy.ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, CreateStrategy.DENY_ABSOLUTE_POINTER);

        var ipsToAllow = allow
            .flatMap(a-> JSONPointer.asArray(a, "#/connection/from"))
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of());

        var ipsToDeny = deny
            .flatMap(d -> JSONPointer.asArray(d, "#/connection/from"))
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of());

        log.debug("IPs to allow {}", ipsToAllow);
        log.debug("IPs to deny {}", ipsToDeny);

        CreateStrategy.scanForRepeatedIP(ipsToAllow, ipsToDeny);

        var toAllow = allow
            .map(a -> topicBindingsFor(principal, a, AclPermissionType.ALLOW))
            .orElseGet(() -> List.of());

        var toDeny = deny
            .map(d -> topicBindingsFor(principal, d, AclPermissionType.DENY))
            .orElseGet(() -> List.of());

        log.info("ACL Bindings to allow {}", toAllow);
        log.info("ACL Bindings to deny {}", toDeny);

        var acl = new ArrayList<AclBinding>();
        acl.addAll(toAllow);
        acl.addAll(toDeny);

        CreateStrategy.scanForRepeatedOperationInAllowDeny(acl);

        var result = admin.createAcls(acl);
        var future = result.all();

        try {
            future.get();
        }catch(InterruptedException | ExecutionException e){
            throw new AclCreateException(e.getMessage(), e);
        }
    }

}
