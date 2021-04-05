package com.github.kattlo.acl.migration;

import static com.github.kattlo.acl.migration.CreateStrategy.*;

import java.util.Objects;

import com.github.kattlo.util.JSONPointer;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class CreateByProducer implements Strategy {

    static final String RELATIVE_POINTER = "#/producer";

    private final JSONObject migration;
    CreateByProducer(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
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

        // TODO create bindings

        // TODO scan for repeated

        // TODO apply

    }

}
