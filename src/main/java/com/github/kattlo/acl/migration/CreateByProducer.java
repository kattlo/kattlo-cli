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
public class CreateByProducer implements Strategy {

    static final String THROW_MSG_TOPIC = "producer topic and top level topic has the same name %s";
    static final String RELATIVE_POINTER = "#/producer";
    static final String IDEMPOTENT_RELATIVE_POINTER = "#/idempotent";
    static final String TRANSACTIONAL_RELATIVE_POINTER = "#/transactional";

    private final JSONObject migration;
    CreateByProducer(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    private List<AclBinding> bindingsFor(String principal, JSONObject access,
        AclPermissionType permission) {

        var producer = JSONPointer.asObject(access, RELATIVE_POINTER).get();
        var topic = JSONPointer.asObject(producer, CreateByTopic.RELATIVE_POINTER)
            .map(t -> t.getString("name"))
            .get();
        var idempotent = JSONPointer.asBoolean(producer, IDEMPOTENT_RELATIVE_POINTER)
            .orElseGet(() -> Boolean.FALSE);
        var transactional = JSONPointer.asObject(producer, TRANSACTIONAL_RELATIVE_POINTER);

        log.debug("Producer topic {}", topic);
        log.debug("Idempotent Producer {}", idempotent);
        log.debug("Transactional Producer {}", transactional);

        var pattern = patternOf(topic, ResourceType.TOPIC);
        log.debug("Topic ResourcePattern {}", pattern);

        var ips = JSONPointer.asArray(access, CONNECTION_RELATIVE_POINTER)
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(WILDCARD_ALL));
        log.debug("List of IPs to {}: {}", permission, ips);

        var read = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.READ, permission))
            .map(entry -> new AclBinding(pattern, entry));

        var write = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.WRITE, permission))
            .map(entry -> new AclBinding(pattern, entry));

        var describe = ips.stream()
            .map(ip ->
                new AccessControlEntry(principal, ip, AclOperation.DESCRIBE, permission))
            .map(entry -> new AclBinding(pattern, entry));

        var idempt = Stream.<AclBinding>of();
        if(idempotent){
            idempt = ips.stream()
                .map(ip ->
                    new AccessControlEntry(principal, ip, AclOperation.IDEMPOTENT_WRITE, permission))
                .map(entry -> new AclBinding(pattern, entry));
        }

        var txPattern = transactional
            .map(t -> t.getString("id"))
            .map(id -> patternOf(id, ResourceType.TRANSACTIONAL_ID));

        var txDescribe = txPattern
            .map(p -> ips.stream()
                .map(ip -> new AccessControlEntry(principal, ip, AclOperation.DESCRIBE, permission))
                .map(entry -> new AclBinding(p, entry))
            )
            .orElseGet(() -> Stream.of());

        var txWrite = txPattern
            .map(p -> ips.stream()
                .map(ip -> new AccessControlEntry(principal, ip, AclOperation.WRITE, permission))
                .map(entry -> new AclBinding(p, entry))
            )
            .orElseGet(() -> Stream.of());

        var result = Stream.concat(Stream.concat(Stream.concat(Stream.concat(Stream.concat(read, write), describe), idempt), txDescribe), txWrite)
        .collect(Collectors.toList());

        log.debug("By Producer result: {}", result);

        return result;
    }

    private void scanForRepeatedTransactional(Optional<JSONObject> operation) {

        var producerTx = operation
            .flatMap(o -> JSONPointer.asObject(o, RELATIVE_POINTER))
            .flatMap(o -> JSONPointer.asObject(o, TRANSACTIONAL_RELATIVE_POINTER))
            .map(t -> t.getString("id"));

        var tx = operation
            .flatMap(o -> JSONPointer.asObject(o, TRANSACTIONAL_RELATIVE_POINTER))
            .map(t -> t.getString("id"))
            .orElseGet(() -> StringUtil.NO_VALUE);

        if(producerTx
            .filter(t -> t.equals(tx))
            .isPresent()){
            throw new AclCreateException("producer transactional and top level on has the same id: " + producerTx);
        }
    }

    @Override
    public void execute(AdminClient admin) {

        var principal = JSONPointer.asString(migration,
            PRINCIPAL_ABSOLUTE_POINTER).get();

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        scanForRepeatedTopic(allow, RELATIVE_POINTER).ifPresent(t -> {
            throw new AclCreateException(String.format(THROW_MSG_TOPIC, t));
        });

        scanForRepeatedTopic(deny, RELATIVE_POINTER).ifPresent(t -> {
            throw new AclCreateException(String.format(THROW_MSG_TOPIC, t));
        });

        scanForRepeatedTransactional(allow);
        scanForRepeatedTransactional(deny);

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
