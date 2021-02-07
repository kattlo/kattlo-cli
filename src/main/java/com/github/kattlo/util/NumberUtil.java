package com.github.kattlo.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

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


    public static String formatted(Object operand) {

        var type = Objects.requireNonNull(operand,
            "provide a non-null operand argument").getClass();

        return (
            type.equals(Double.class) || type.equals(Float.class)
            ? new DecimalFormat("##0.0#########", new DecimalFormatSymbols(Locale.US)).format(operand)
            : operand.toString()
        );

    }
}
