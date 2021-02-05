package com.github.kattlo.core.configuration.condition;

import java.util.Objects;

/**
 * @author fabiojose
 */
public interface Condition {

    /**
     * @throws IllegalArgumentException When value argument contains an invalid reference
     */
    boolean execute(Object value);

    static Condition byPass() {
        return new ByPass();
    }

    static Condition of(String condition, Object operand) {

        switch (Objects.requireNonNull(condition).trim().toLowerCase()) {
            case "==":
                return new Equals(operand);

            case "!=":
                return new NotEquals(operand);

            case ">":
                return new Greater(operand);

            case ">=":
                return new GreaterOrEquals(operand);

            case "<":
                return new Less(operand);

            case "<=":
                return new LessOrEquals(operand);

            case "in":
                return new In(operand);

            case "!in":
                return new NotIn(operand);

            case "regex":
                return new TextPattern(operand);

            default:
                throw new IllegalArgumentException(condition);
        }
    }

}
