package com.github.kattlo.core.configuration.condition;

/**
 * @author fabiojose
 */
public class ByPass implements Condition {

    @Override
    public boolean execute(Object value) {
        return true;
    }

    @Override
    public String toString(){
        return "bypass";
    }
}
