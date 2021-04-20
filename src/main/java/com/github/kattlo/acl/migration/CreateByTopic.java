package com.github.kattlo.acl.migration;

import static com.github.kattlo.acl.migration.CreateStrategy.*;

import java.util.List;
import java.util.stream.Collectors;

import com.github.kattlo.util.JSONPointer;
import com.github.kattlo.util.JSONUtil;

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
public class CreateByTopic extends AbstractCreate {

    static final String RELATIVE_POINTER = "#/topic";
    static final String NAME_ATTRIBUTE = "name";

    CreateByTopic(JSONObject migration){
        super(migration);
    }

    @Override
    protected List<AclBinding> bindingsFor(String principal, JSONObject access,
            AclPermissionType permission){

        var topic = JSONPointer.asObject(access, RELATIVE_POINTER).get();
        var topicName = topic.getString(NAME_ATTRIBUTE);

        var operationsJson = JSONPointer.asArray(topic, OPERATIONS_RELATIVE_POINTER);
        log.debug("Operations to {}: {}", permission, operationsJson);

        var pattern = patternOf(topicName, ResourceType.TOPIC);
        log.debug("Topic ResourcePattern {}", pattern);

        List<AclOperation> operations;
        try {
            operations = parseOperation(operationsJson);
            log.debug("Parsed operations to {}: {}", permission, operations);

        }catch(IllegalArgumentException e){
            throw new AclCreateException("Some accesses to " + permission + " are invalid", e);
        }

        var ips = JSONPointer.asArray(access, CONNECTION_RELATIVE_POINTER)
            .map(JSONUtil::asString)
            .orElseGet(() -> List.of(WILDCARD_ALL));
        log.debug("List of IPs to {}: {}", permission, ips);

        return operations.stream()
            .flatMap(operation ->
                ips.stream()
                    .map(ip -> new AccessControlEntry(principal, ip, operation, permission))
            )
            .map(entry -> new AclBinding(pattern, entry))
            .collect(Collectors.toList());

    }

    @Override
    protected String deed() {
        return "create";
    }

}
