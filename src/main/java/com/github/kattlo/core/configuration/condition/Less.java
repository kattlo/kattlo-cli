package com.github.kattlo.core.configuration.condition;

import com.github.kattlo.util.MachineReadableSupport;
import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class Less implements Condition {

    private final MachineReadableSupport support;

    public Less(Object operand) {
        this.support = MachineReadableSupport.of(operand);

        if(!NumberUtil.isNumber(support.getMachineReadable())){
            throw new IllegalArgumentException("operand must be a number instance: " + operand.getClass());
        }
    }

    @Override
    public boolean execute(Object value) {
        if(!NumberUtil.isNumber(value)){
            throw new IllegalArgumentException("value must be a number instance: " + value);
        }

        return NumberUtil.compare((Number)support.getMachineReadable(),
            (Number)value, (v1, v2) -> v1 > v2);
    }

    @Override
    public String toString() {
        return "<" +
            support.getHumanReadable()
                .orElseGet(() ->
                    NumberUtil.formatted(support.getMachineReadable()));
    }
}
