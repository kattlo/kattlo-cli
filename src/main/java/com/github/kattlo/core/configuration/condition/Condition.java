package com.github.kattlo.core.configuration.condition;

/**
 * @author fabiojose
 */
public interface Condition {

    /**
     * @throws IllegalArgumentException When value argument contains an invalid reference
     */
    boolean execute(Object value);

}
