package com.github.kattlo.core.configuration.condition;

import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class GreaterOrEquals implements NumberCondition {

    private final Object operand;
    public GreaterOrEquals(Object operand) {
        if(!NumberUtil.isNumber(operand)){
            throw new IllegalArgumentException("operand must be a number instance: " + operand);
        }
        this.operand = operand;
    }

    @Override
    public boolean execute(Object value) {
        if(!NumberUtil.isNumber(value)){
            throw new IllegalArgumentException("value must be a number instance: " + value);
        }

        return compare((Number)operand, (Number)value, (v1, v2) -> v1 <= v2);
    }

    @Override
    public String toString() {
        return ">=" + operand;
    }
}
