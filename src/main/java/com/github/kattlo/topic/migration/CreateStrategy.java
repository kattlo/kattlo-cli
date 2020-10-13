package com.github.kattlo.topic.migration;

import com.github.kattlo.topic.yaml.TopicOperation;

import org.apache.kafka.clients.admin.AdminClient;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author fabiojose
 */
@RequiredArgsConstructor
public class CreateStrategy implements Strategy {

    @NonNull
    private final TopicOperation operation;

    @Override
    public void execute(AdminClient admin) {

    }

}
