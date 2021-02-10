package com.github.kattlo.core.configuration.condition;

import java.util.List;
import java.util.Objects;

/**
 * @author fabiojose
 */
public class In implements Condition {

    private final List<Object> operand;

    @SuppressWarnings("unchecked")
    public In(Object operand) {
        if(!(operand instanceof List)) {
            throw new IllegalArgumentException("operand must be an instance of java.util.List: " + operand);
        }

        this.operand = (List<Object>)operand;
    }

    @Override
    public boolean execute(Object value) {
        Objects.requireNonNull(value, "provide a not-null instance for value argument");

        return operand.contains(value);
    }

    @Override
    public String toString() {
        return "in " + operand;
    }
}
