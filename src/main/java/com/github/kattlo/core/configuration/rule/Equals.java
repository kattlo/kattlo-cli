package com.github.kattlo.core.configuration.rule;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@AllArgsConstructor
public class Equals {

    @NonNull
    private final Object operand;

    public boolean execute(Object value) {
        return Objects.equals(this.operand, value);
    }

}
