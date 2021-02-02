package com.github.kattlo.core.configuration.condition;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@AllArgsConstructor
public class NotEquals {

    @NonNull
    private final Object operand;

    public boolean execute(Object value) {
        return !operand.equals(value);
    }
}
