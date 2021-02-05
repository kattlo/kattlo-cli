package com.github.kattlo.util;

import lombok.experimental.UtilityClass;

/**
 * @author fabiojose
 */
@UtilityClass
public class NumberUtil {

    public static boolean isNumber(Object value) {

        return (value instanceof Integer)
            || (value instanceof Short)
            || (value instanceof Long)
            || (value instanceof Float)
            || (value instanceof Double);
    }
}
