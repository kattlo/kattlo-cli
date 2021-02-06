package com.github.kattlo.core.configuration.condition;

/**
 * @author fabiojose
 */
public class NotIn implements Condition {

    private final In in;

    public NotIn(Object operand) {
        this.in = new In(operand);
    }

    @Override
    public boolean execute(Object value) {
        return !in.execute(value);
    }

    @Override
    public String toString() {
        return "!" + super.toString();
    }
}
