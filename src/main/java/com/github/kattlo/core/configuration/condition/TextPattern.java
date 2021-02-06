package com.github.kattlo.core.configuration.condition;

import java.util.regex.Pattern;

/**
 * @author fabiojose
 */
public class TextPattern implements Condition {

    private final Pattern operand;
    public TextPattern(Object operand){
        if(!(operand instanceof String)){
            throw new IllegalArgumentException("operand must be an instance of String: " + operand);
        }

        this.operand = Pattern.compile((String)operand);
    }

    @Override
    public boolean execute(Object value) {
        if(!(value instanceof String)){
            throw new IllegalArgumentException("value must be an instance of String: " + value);
        }

        var matcher = operand.matcher((String)value);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return operand.toString();
    }
}
