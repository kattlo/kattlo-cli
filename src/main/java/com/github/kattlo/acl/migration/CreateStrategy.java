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
import com.github.kattlo.util.LazyLogging;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
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

    static final String WILDCARD_ALL = "*";

    static final String CREATE_ATT = "create";
    static final String PRINCIPAL_ABSOLUTE_POINTER = "/create/to/principal";

    static final String ALLOW_ABSOLUTE_POINTER = "/create/allow";
    static final String DENY_ABSOLUTE_POINTER = "/create/deny";

    static final String CONNECTION_RELATIVE_POINTER = "#/connection/from";

    static final String OPERATIONS_RELATIVE_POINTER = "#/operations";

    private final JSONObject migration;

    CreateStrategy(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    static ResourcePattern patternOf(String name, ResourceType resource) {
        PatternType type = PatternType.LITERAL;
        String actualName = name;

        if(NAME_PATTERN.matcher(name).matches()){
            type = PatternType.PREFIXED;
            actualName = name.substring(0, name.indexOf(ALL_PATTERN));

        } else if(name.equals(ALL_PATTERN)){
            type = PatternType.MATCH;
        }

        log.debug("The pattern of {} is {}", name, type);

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
    static void scanForRepeatedOperationInAllowDeny(List<AclBinding> acl) {
        log.debug("ACLs *not* distincted by operation {}", acl);

        var distincted = acl.stream()
            .map(AclBindingWrapper::new)
            .distinct()
            .map(AclBindingWrapper::unwrap)
            .collect(Collectors.toList());

        log.debug("ACLs distincted by operation {}", distincted);

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

    static void scanForRepeatedIP(List<String> allow, List<String> deny) {

        var repeated =
            allow.stream()
                .filter(ip -> !(ip.equals(ALL_PATTERN)))
                .filter(deny::contains)
                .collect(Collectors.toList());

        if(!repeated.isEmpty()){
            throw new AclCreateException("repeated IPs in allow and deny " + repeated);
        }
    }

    static List<AclOperation> parseOperation(Optional<JSONArray> operations) {
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

    static List<String> connectionIPs(Optional<JSONObject> o) {

        return o.flatMap(d -> JSONPointer.asArray(d, CONNECTION_RELATIVE_POINTER))
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of());
    }

    static void apply(List<AclBinding> allow, List<AclBinding> deny, AdminClient admin) {

        log.debug("ACL Bindings to allow {}", allow);
        log.debug("ACL Bindings to deny {}", deny);

        var acl = new ArrayList<AclBinding>();
        acl.addAll(allow);
        acl.addAll(deny);

        scanForRepeatedOperationInAllowDeny(acl);

        var result = admin.createAcls(acl);
        var future = result.all();

        try {
            future.get();
        }catch(InterruptedException | ExecutionException e){
            throw new AclCreateException(e.getMessage(), e);
        }
    }

    @Override
    public void execute(AdminClient admin) {
        Objects.requireNonNull(admin);

        log.debug("Creating ACL based on {}", migration);
        log.debug("Creating ACL using AdminClient {}", admin);

        var principal = JSONPointer.asString(migration, PRINCIPAL_ABSOLUTE_POINTER).get();
        log.debug("Creating ACL for Principal {}", principal);

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        log.debug("IPs to allow {}", LazyLogging.of(() -> connectionIPs(allow)));
        log.debug("IPs to deny {}", LazyLogging.of(() -> connectionIPs(deny)));

        if(JSONPointer.hasRelativeObjectPointer(allow, deny,
                CreateByTopic.RELATIVE_POINTER)){

            log.debug("Creating ACL by Topic");
            new CreateByTopic(migration).execute(admin);
        }

        if(JSONPointer.hasRelativeObjectPointer(allow, deny,
                CreateByProducer.RELATIVE_POINTER)){

            log.debug("Creating ACL by Producer");
            new CreateByProducer(migration).execute(admin);
        }

    }

}
