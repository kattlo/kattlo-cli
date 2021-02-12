package com.github.kattlo.core.configuration.condition;

import com.github.kattlo.util.NumberUtil;

/**
 * @author fabiojose
 */
public class NotEquals implements Condition {

    private final Equals equals;

    public NotEquals(Object operand){
        this.equals = new Equals(operand);
    }

    @Override
    public boolean execute(Object value) {
        return !equals.execute(value);
    }

    @Override
    public String toString(){
        return "!=" +
            equals.getSupport().getHumanReadable()
                .orElseGet(() ->  NumberUtil.formatted(equals.getSupport().getMachineReadable()));
    }
}
