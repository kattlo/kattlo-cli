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
        var operand = support.getMachineReadable();

        if(NumberUtil.isNumber(operand)){
            if(NumberUtil.isNumber(value)){
                return NumberUtil.compare((Number)operand, (Number)value,
                    (v1, v2) -> v1 == v2);
            } else {
                throw new IllegalArgumentException("value must be a number instance: " + value);
            }
        }

        return Objects.equals(support.getMachineReadable(), value);
    }

    @Override
    public String toString() {
        return "==" +
            support.getHumanReadable()
                .orElseGet(() ->  NumberUtil.formatted(support.getMachineReadable()));
    }
}
