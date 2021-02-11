package com.github.kattlo.core.configuration.condition;

import java.util.Objects;

import com.github.kattlo.util.MachineReadableSupport;
import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class Equals implements Condition {

    private final MachineReadableSupport support;

    public Equals(Object operand) {
        this.support = MachineReadableSupport.of(operand);
    }

    @Override
    public boolean execute(Object value) {
        return Objects.equals(support.getMachineReadable(), value);
    }

    @Override
    public String toString() {
        return "==" +
            support.getHumanReadable()
                .orElseGet(() ->  NumberUtil.formatted(support.getMachineReadable()));
    }
}
