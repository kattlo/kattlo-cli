package com.github.kattlo.core.configuration.rule;

import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class GreaterOrEquals {

    private final Object operand;
    public GreaterOrEquals(Object operand) {
        if(!NumberUtil.isNumber(operand)){
            throw new IllegalArgumentException("operand must be a number instance: " + operand);
        }
        this.operand = operand;
    }

    private boolean compareTo(Number operand, Number value, Class<?> type) {
        return (
            type.equals(Long.class)
            ? Long.compare(operand.longValue(), value.longValue()) <= 0

            : type.equals(Integer.class)
              ? Integer.compare(operand.intValue(), value.intValue()) <= 0

              : type.equals(Short.class)
                ? Short.compare(operand.shortValue(), value.shortValue()) <= 0

                : type.equals(Float.class)
                  ? Float.compare(operand.floatValue(), value.floatValue()) <= 0

                  : type.equals(Double.class)
                    ? Double.compare(operand.doubleValue(), value.doubleValue()) <= 0
                    : false
        );
    }

    public boolean execute(Object value) {
        if(!NumberUtil.isNumber(value)){
            throw new IllegalArgumentException("value must be a number instance: " + value);
        }

        return compareTo((Number)operand, (Number)value, operand.getClass());
    }
}
