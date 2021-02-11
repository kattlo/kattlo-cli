package com.github.kattlo.core.configuration.condition;

import com.github.kattlo.util.MachineReadableSupport;
import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class GreaterOrEquals implements NumberCondition {

    private final MachineReadableSupport support;

    public GreaterOrEquals(Object operand) {
        this.support = MachineReadableSupport.of(operand);
    }

    @Override
    public boolean execute(Object value) {
        if(!NumberUtil.isNumber(value)){
            throw new IllegalArgumentException("value must be a number instance: " + value);
        }

        return compare((Number)support.getMachineReadable(),
            (Number)value, (v1, v2) -> v1 <= v2);
    }

    @Override
    public String toString() {
        return ">=" +
            support.getHumanReadable()
                .orElseGet(() ->
                    NumberUtil.formatted(support.getMachineReadable()));
    }
}
