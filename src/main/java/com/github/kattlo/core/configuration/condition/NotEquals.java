package com.github.kattlo.core.configuration.condition;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@AllArgsConstructor
public class NotEquals implements Condition {

    @NonNull
    private final Object operand;

    @Override
    public boolean execute(Object value) {
        return !operand.equals(value);
    }

    @Override
    public String toString(){
        return "!=" + operand;
    }
}
