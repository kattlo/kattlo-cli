package com.github.kattlo.topic.migration;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author fabiojose
 */
@RequiredArgsConstructor(access = AccessLevel.MODULE)
public class RemoveStrategy implements Strategy {

    @NonNull
    private final TopicOperation operation;

    @Override
    public void execute(AdminClient admin) {
        // TODO Auto-generated method stub

    }

}
