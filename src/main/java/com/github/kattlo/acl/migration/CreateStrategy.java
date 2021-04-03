package com.github.kattlo.acl.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.kattlo.util.JSONPointer;
import com.github.kattlo.util.JSONUtil;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.json.JSONArray;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class CreateStrategy implements Strategy {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w\\.\\-]*\\*$");
    private static final String ALL_PATTERN = "*";

    static final String CREATE_ATT = "create";
    static final String PRINCIPAL_ABS_POINTER = "/create/to/principal";

    static final String ALLOW_ABSOLUTE_POINTER = "/create/allow";
    static final String DENY_ABSOLUTE_POINTER = "/create/deny";

    private final JSONObject migration;

    CreateStrategy(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    private ResourcePattern patternOf(String name, ResourceType resource) {
        PatternType type = PatternType.LITERAL;
        String actualName = name;

        if(NAME_PATTERN.matcher(name).matches()){
            type = PatternType.PREFIXED;
            actualName = name.substring(0, name.indexOf(ALL_PATTERN));

        } else if(name.equals(ALL_PATTERN)){
            type = PatternType.MATCH;
        }

        log.info("The pattern of {} is {}", name, type);

        return new ResourcePattern(resource, actualName, type);
    }

    private static class AclBindingWrapper {

        private final AclBinding binding;
        AclBindingWrapper(AclBinding binding){
            this.binding = binding;
        }

        AclBinding unwrap(){
            return binding;
        }

        @Override
        public boolean equals(Object otherWrapper){
            if(otherWrapper instanceof AclBindingWrapper){
                var other = ((AclBindingWrapper)otherWrapper).binding;

                return binding.pattern().name().equals(other.pattern().name())
                    && binding.entry().permissionType().equals(other.entry().permissionType())
                    && binding.entry().operation().equals(other.entry().operation());
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = 31 * hash + binding.pattern().name().hashCode();
            hash = 31 * hash + binding.entry().permissionType().hashCode();
            hash = 31 * hash + binding.entry().operation().hashCode();

            return hash;
        }
    }

    /**
     * allow and deny should not has the same operations
     */
    private void scanForRepeatedOperationInAllowDeny(List<AclBinding> acl) {
        log.info("ACLs *not* distincted by operation {}", acl);

        var distincted = acl.stream()
            .map(AclBindingWrapper::new)
            .distinct()
            .map(AclBindingWrapper::unwrap)
            .collect(Collectors.toList());

        log.info("ACLs distincted by operation {}", distincted);

        var byOperation = distincted.stream()
            .collect(Collectors.groupingBy(v -> v.entry().operation()));

        var repeated = byOperation.entrySet().stream()
            .filter(kv -> kv.getValue().size() > 1)
            .map(kv -> kv.getValue().iterator().next())
            .map(a -> a.entry().operation())
            .collect(Collectors.toList());

        if(!repeated.isEmpty()){
            throw new AclCreateException("repeated operations in allow and deny " + repeated);
        }
    }

    private void scanForRepeatedIP(List<String> allow, List<String> deny) {

        var repeated =
            allow.stream()
                .filter(ip -> !(ip.equals(ALL_PATTERN)))
                .filter(deny::contains)
                .collect(Collectors.toList());

        if(!repeated.isEmpty()){
            throw new AclCreateException("repeated IPs in allow and deny " + repeated);
        }
    }

    private List<AclOperation> parseOperation(Optional<JSONArray> operations) {
        return operations
            .map(JSONArray::iterator)
            .map(i -> Spliterators.spliteratorUnknownSize(i, Spliterator.NONNULL))
            .map(s -> StreamSupport.stream(s, false))
            .map(accesses ->
                accesses
                .map(Object::toString)
                .map(access -> access.toUpperCase(Locale.ROOT))
                .map(access -> AclOperation.valueOf(access))
                .collect(Collectors.toList())
            )
            .orElse(List.of());
    }

    private List<AclBinding> topicBindingsFor(String principal, JSONObject access,
            AclPermissionType permission){

        var topic = JSONPointer.asObject(access, "#/topic").get();
        var topicName = topic.getString("name");

        var operationsJson = JSONPointer.asArray(topic, "#/operations");
        log.info("Operations to {}: {}", permission, operationsJson);

        var pattern = patternOf(topicName, ResourceType.TOPIC);
        log.info("Topic ResourcePattern {}", pattern);

        List<AclOperation> operations;
        try {
            operations = parseOperation(operationsJson);
            log.info("Parsed operations to {}: {}", permission, operations);

        }catch(IllegalArgumentException e){
            throw new AclCreateException("Some accesses to " + permission + " are invalid", e);
        }

        var ips = JSONPointer.asArray(access, "#/connection/from")
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(ALL_PATTERN));
        log.info("List of IPs to {}: {}", permission, ips);

        return operations.stream()
            .flatMap(operation ->
                ips.stream()
                    .map(ip -> new AccessControlEntry(principal, ip, operation, permission))
            )
            .map(entry -> new AclBinding(pattern, entry))
            .collect(Collectors.toList());

    }

    private void aclByTopic(String principal, AdminClient admin)
            throws InterruptedException, ExecutionException {

        var allow = JSONPointer.asObject(migration, "/create/allow");
        var deny = JSONPointer.asObject(migration, "/create/deny");

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

        scanForRepeatedIP(ipsToAllow, ipsToDeny);

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

        scanForRepeatedOperationInAllowDeny(acl);

        var result = admin.createAcls(acl);
        var future = result.all();
        future.get();
    }


    @Override
    public void execute(AdminClient admin) {
        Objects.requireNonNull(admin);

        log.debug("Creating ACL based on {}", migration);
        log.debug("Creating ACL using AdminClient {}", admin);

        var principal = JSONPointer.asString(migration, PRINCIPAL_ABS_POINTER).get();
        log.debug("Creating ACL for Principal {}", principal);

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        try {
            if(JSONPointer.hasRelativeObjectPointer(allow, deny, CreateByTopic.RELATIVE_POINTER)){
                log.debug("Creating ACL by Topic");

                // TODO move code to CreateByTopic
                aclByTopic(principal, admin);
            }

            if(JSONPointer.hasRelativeObjectPointer(allow, deny, CreateByProducer.RELATIVE_POINTER)){
                log.debug("Creating ACL by Producer");

                new CreateByProducer(migration).execute(admin);
            }

        }catch(InterruptedException | ExecutionException e) {
            throw new AclCreateException(e.getMessage(), e);
        }
    }

}
