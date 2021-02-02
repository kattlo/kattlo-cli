package com.github.kattlo.core.configuration;

import lombok.Builder;

/**
 * @author fabiojose
 */
@Builder(toBuilder = true)
public class Rule {

    private String property;
    private Object condition;
    private Object operand;
    private Object valueType;

    /*
    ???
        partitions.greaterOrEqualsTo()
    */

    public boolean applyTo(Object value){

        return false;
    }

    public static Rule of() {
        return null;
    }
}
