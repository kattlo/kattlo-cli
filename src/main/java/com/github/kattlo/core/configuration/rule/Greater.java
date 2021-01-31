package com.github.kattlo.core.configuration.rule;

import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class Greater {

    private final Object operand;
    public Greater(Object operand) {
        if(!NumberUtil.isNatural(operand)){
            throw new IllegalArgumentException("operand must be a number instance: " + operand);
        }
        this.operand = operand;
    }

    private boolean longCompareTo(Long operand, Object value) {
        return (
            (value instanceof Long)
            ? operand.compareTo((Long)value) < 0

            : (value instanceof Integer)
              ? operand.compareTo(((Integer)value).longValue()) < 0

              : (value instanceof Short)
                ? operand.compareTo(((Short)value).longValue()) < 0
                : false
        );
    }

    private boolean intCompareTo(Integer operand, Object value) {
        return (
            (value instanceof Integer)
            ? operand.compareTo((Integer)value) < 0

            : (value instanceof Long)
              ? operand.compareTo(((Long)value).intValue()) < 0

              : (value instanceof Short)
                ? operand.compareTo(((Short)value).intValue()) < 0
                : false
        );
    }

    private boolean shortCompareTo(Short operand, Object value){

        return (
            (value instanceof Short)
            ? operand.compareTo((Short)value) < 0

            : (value instanceof Integer)
              ? operand.compareTo(((Integer)value).shortValue()) < 0

              : (value instanceof Long)
                ? operand.compareTo(((Long)value).shortValue()) < 0
                : false
        );
    }

    public boolean execute(Object value) {
        if(!NumberUtil.isNatural(value)){
            throw new IllegalArgumentException("value must be a number instance: " + value);
        }

        return (operand instanceof Long)
               ? longCompareTo((Long)operand, value)
               : (operand instanceof Integer)
                 ? intCompareTo((Integer)operand, value)
                 : (operand instanceof Short)
                   ? shortCompareTo((Short)operand, value)
                   : false;
    }
}
