package com.github.kattlo.core.backend.file.yaml.model;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * @author fabiojose
 */
@Data
@RegisterForReflection
public class State {

    private List<Current> topics;

}
