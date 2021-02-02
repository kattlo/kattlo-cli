package com.github.kattlo.core.configuration.condition;

/**
 * @author fabiojose
 */
public interface NumberCondition extends Condition {

    default boolean compare(Number operand, Number value, Comparation c) {

        var type = operand.getClass();

        return (
            type.equals(Long.class)
            ? c.compare(Long.compare(operand.longValue(), value.longValue()), 0)

            : type.equals(Integer.class)
              ? c.compare(Integer.compare(operand.intValue(), value.intValue()), 0)

              : type.equals(Short.class)
                ? c.compare(Short.compare(operand.shortValue(), value.shortValue()), 0)

                : type.equals(Float.class)
                  ? c.compare(Float.compare(operand.floatValue(), value.floatValue()), 0)

                  : type.equals(Double.class)
                    ? c.compare(Double.compare(operand.doubleValue(), value.doubleValue()), 0)

                    : Boolean.FALSE
        );
    }

    @FunctionalInterface
    public static interface Comparation {
        boolean compare(int v1, int v2);
    }
}
