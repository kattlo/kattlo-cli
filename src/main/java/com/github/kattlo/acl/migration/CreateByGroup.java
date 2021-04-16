package com.github.kattlo.acl.migration;

import java.util.Objects;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;

/**
 * @author fabiojose
 */
public class CreateByGroup implements Strategy {

    static final String RELATIVE_POINTER = "#/group";

    private final JSONObject migration;
    CreateByGroup(JSONObject migration) {
        this.migration = Objects.requireNonNull(migration);
    }

    @Override
    public void execute(AdminClient admin) {

    }
}
