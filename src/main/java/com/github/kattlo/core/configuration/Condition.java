package com.github.kattlo.core.configuration;

import java.util.Objects;

/**
 * @author fabiojose
 */
public enum Condition {

    EQUALS,
    NOT_EQUALS,
    GREATER,
    GREATER_OR_EQUALS,
    LESS,
    LESS_OR_EQUALS,
    IN,
    NOT_IN;

    public static Condition of(String condition) {

        switch (Objects.requireNonNull(condition).trim().toLowerCase()) {
            case "==":
                return EQUALS;

            case "!=":
                return NOT_EQUALS;

            case ">":
                return GREATER;

            case ">=":
                return GREATER_OR_EQUALS;

            case "<":
                return LESS;

            case "<=":
                return LESS_OR_EQUALS;

            case "in":
                return IN;

            case "!in":
                return NOT_IN;

            default:
                throw new IllegalArgumentException(condition);
        }
    }
}
