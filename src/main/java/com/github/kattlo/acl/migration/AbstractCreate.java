package com.github.kattlo.acl.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.kattlo.util.JSONPointer;
import com.github.kattlo.util.JSONUtil;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclPermissionType;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public abstract class AbstractCreate implements Strategy {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w\\.\\-]*\\*$");
    private static final String ALL_PATTERN = "*";

    private static final String WILDCARD_ALL = "*";

    private static final String CREATE_ATT = "create";
    private static final String PRINCIPAL_ABSOLUTE_POINTER = "/create/to/principal";

    private static final String ALLOW_ABSOLUTE_POINTER = "/create/allow";
    private static final String DENY_ABSOLUTE_POINTER = "/create/deny";

    private static final String CONNECTION_RELATIVE_POINTER = "#/connection/from";

    private static final String OPERATIONS_RELATIVE_POINTER = "#/operations";

    protected abstract List<AclBinding> bindingsFor(String principal, JSONObject access,
            AclPermissionType permission);

    protected abstract String deed();

    private final JSONObject migration;
    public AbstractCreate(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
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

    //TODO complex IP scan
   // static void scanForRepeatedIPInAllowAndDeny(Optional<JSONObject> allow, Optional<JSONObject> deny) {

   //     if(allow.isPresent() && deny.isPresent()) {
   //         // per resource: topic, group, producer, consumer, cluster and transacional

   //         // group by ip,

   //         // operation, if resources are the same
   //     }
   // }

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
                    && binding.entry().operation().equals(other.entry().operation())
                    && binding.entry().host().equals(other.entry().host());
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = 31 * hash + binding.pattern().name().hashCode();
            hash = 31 * hash + binding.entry().permissionType().hashCode();
            hash = 31 * hash + binding.entry().operation().hashCode();
            hash = 31 * hash + binding.entry().host().hashCode();

            return hash;
        }
    }

    /**
     * allow and deny should not has the same operations
     * connecting from the same host ip
     */
    static void scanForRepeatedOperationInAllowDeny(List<AclBinding> acl) {
        log.info("ACLs *not* distincted by operation {}", acl);

        // distinct by: resource name, permission, operation and host ip
        var distincted = acl.stream()
            .map(AclBindingWrapper::new)
            .distinct()
            .map(AclBindingWrapper::unwrap)
            .collect(Collectors.toList());

        log.info("ACLs distincted by operation and host ip {}", distincted);

        // Group by host ip
        var byHostIp = distincted.stream()
            .collect(Collectors.groupingBy(v -> v.entry().host()));

        // scanfor repeated within each host ip group
        var repeated = byHostIp.entrySet().stream()
            .map(ips ->
                Map.entry(ips.getKey(),
                    ips.getValue().stream()
                        .collect(
                            Collectors.groupingBy(v -> v.pattern().name(),
                                Collectors.groupingBy(v -> v.entry().operation())
                            )
                        )
                        .entrySet().stream()
                        .map(kv ->
                            Map.entry(kv.getKey(),
                                kv.getValue().entrySet().stream()
                                    .filter(m -> m.getValue().size() > 1)
                                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
                            )
                        )
                        .filter(kv -> !kv.getValue().isEmpty())
                        .map(kv -> kv.getKey() + ": " + kv.getValue().keySet().iterator().next())
                        .collect(Collectors.toList())
                )
            )
            .filter(kv -> !kv.getValue().isEmpty())
            .map(kv -> kv.getKey() + "-> " + kv.getValue().toString())
            .collect(Collectors.toList());

        if(!repeated.isEmpty()){
            throw new AclCreateException("repeated operations in allow and deny " + repeated);
        }
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

    static List<String> connectionIPs(Optional<JSONObject> o) {

        return o.flatMap(d -> JSONPointer.asArray(d, CONNECTION_RELATIVE_POINTER))
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of());
    }

    @Override
    public void execute(AdminClient admin) {

        var principal = JSONPointer.asString(migration,
            PRINCIPAL_ABSOLUTE_POINTER).get();

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        var ipsToAllow = connectionIPs(allow);
        var ipsToDeny = connectionIPs(deny);

        scanForRepeatedIP(ipsToAllow, ipsToDeny);

        var toAllow = allow
            .map(a -> bindingsFor(principal, a, AclPermissionType.ALLOW))
            .orElseGet(() -> List.of());

        var toDeny = deny
            .map(d -> bindingsFor(principal, d, AclPermissionType.DENY))
            .orElseGet(() -> List.of());

        apply(toAllow, toDeny, admin);
    }
}
