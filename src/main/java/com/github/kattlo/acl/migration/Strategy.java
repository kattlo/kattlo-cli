package com.github.kattlo.acl.migration;

import java.util.Objects;

import org.apache.kafka.clients.admin.AdminClient;
import org.json.JSONObject;

/**
 * @author fabiojose
 */
public interface Strategy {

    void execute(AdminClient admin);

    static Strategy of(JSONObject migration) {
        Objects.requireNonNull(migration);

        if(null!= migration.getJSONObject(CreateStrategy.CREATE_ATT)){
            return new CreateStrategy(migration);

        } else {
            throw new IllegalArgumentException("Unknown migration");
        }

    }
}
