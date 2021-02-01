package com.github.kattlo.core.configuration.rule;

/**
 * @author fabiojose
 */
public class NotIn {

    private final In in;

    public NotIn(Object operand) {
        this.in = new In(operand);
    }

    public boolean execute(Object value) {
        return !in.execute(value);
    }
}
