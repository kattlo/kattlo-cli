package com.github.kattlo.acl.migration;

import java.util.Objects;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;

/**
 * @author fabiojose
 */
public class CreateByTopic implements Strategy {

    static final String RELATIVE_POINTER = "#/topic";

    private final JSONObject migration;
    CreateByTopic(JSONObject migration){
        this.migration = Objects.requireNonNull(migration);
    }

    @Override
    public void execute(AdminClient admin) {

    }

}
