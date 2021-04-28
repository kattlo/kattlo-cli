package com.github.kattlo.acl.migration;

import static com.github.kattlo.acl.migration.CreateStrategy.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class CreateByHost extends AbstractCreate {

    static final String RELATIVE_POINTER = "#/connection";
    private static final String KEY = "connection";
    private static final String ALL_PATTERN = "*";
    private static final String CONNECTION_RELATIVE_POINTER = "#/connection/from";

    public CreateByHost(JSONObject migration) {
        super(migration);
    }

    static boolean hasOnlyConnectionFrom(Optional<JSONObject> allow,
        Optional<JSONObject> deny) {

        return allow.filter(a -> a.length() == 1)
            .map(a -> a.opt(KEY))
            .map(Objects::nonNull)
            .orElseGet(() -> false)
            ||
        deny.filter(d -> d.length() == 1)
            .map(d -> d.opt(KEY))
            .map(Objects::nonNull)
            .orElseGet(() -> false);

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

    static List<String> connectionIPs(Optional<JSONObject> o) {

        return o.flatMap(d -> JSONPointer.asArray(d, CONNECTION_RELATIVE_POINTER))
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of());
    }

    private AclBinding bindingFor(String principal, String ip, ResourcePattern pattern,
            AclPermissionType permission){

        return new AclBinding(pattern,
            new AccessControlEntry(principal, ip, AclOperation.ALL, permission));

    }

    private ResourcePattern patternOf(ResourceType type){
        return new ResourcePattern(type, WILDCARD_ALL, PatternType.LITERAL);
    }

    @Override
    protected List<AclBinding> bindingsFor(String principal, JSONObject access,
            AclPermissionType permission) {

        var ips = JSONPointer.asArray(access, CONNECTION_RELATIVE_POINTER)
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(WILDCARD_ALL));
        log.debug("List of IPs to {}: {}", permission, ips);

        var topic = ips.stream()
            .map(ip -> bindingFor(principal, ip, patternOf(ResourceType.TOPIC),
                permission));

        var group = ips.stream()
            .map(ip -> bindingFor(principal, ip, patternOf(ResourceType.GROUP),
                permission));

        var transactional = ips.stream()
            .map(ip -> bindingFor(principal, ip, patternOf(ResourceType.TRANSACTIONAL_ID),
                permission));

        var cluster = ips.stream()
            .map(ip -> bindingFor(principal, ip, patternOf(ResourceType.CLUSTER),
                permission));

        var result = Stream.concat(Stream.concat(Stream.concat(topic, group), transactional), cluster)
                .collect(Collectors.toList());
        log.debug("By Host IP result: {}", result);

        return result;
    }

    @Override
    protected String deed() {
        return "create";
    }

    public void execute(AdminClient admin){

        var allow = JSONPointer.asObject(migration, ALLOW_ABSOLUTE_POINTER);
        var deny = JSONPointer.asObject(migration, DENY_ABSOLUTE_POINTER);

        var ipsToAllow = connectionIPs(allow);
        var ipsToDeny = connectionIPs(deny);

        scanForRepeatedIP(ipsToAllow, ipsToDeny);

        super.execute(admin);
    }
}
