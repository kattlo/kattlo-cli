package com.github.kattlo.acl.migration;

import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;

/**
 * @author fabiojose
 */
public class CreateByProducer implements Strategy {

    static final String RELATIVE_POINTER = "#/producer";

    private final JSONObject migration;
    CreateByProducer(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    @Override
    public void execute(AdminClient admin) {

    }

    public void execute(String principal, Optional<JSONObject> allow,
        Optional<JSONObject> deny, AdminClient admin) {

    }

}
