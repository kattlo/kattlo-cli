package com.github.kattlo.core.configuration.condition;

import java.util.Objects;

import com.github.kattlo.util.NumberUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@AllArgsConstructor
public class Equals implements Condition {

    @NonNull
    private final Object operand;

    @Override
    public boolean execute(Object value) {
        return Objects.equals(this.operand, value);
    }

    @Override
    public String toString() {
        return "==" + NumberUtil.formatted(operand);
    }
}
