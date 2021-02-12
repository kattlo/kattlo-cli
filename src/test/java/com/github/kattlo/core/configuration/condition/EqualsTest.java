package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EqualsTest {

    @Test
    public void should_throw_when_operand_is_number_and_value_is_not() {

        var equals = new Equals("2seconds");

        assertThrows(IllegalArgumentException.class, () ->
            equals.execute("s"));
    }

    @Test
    public void should_result_true_when_string_equals() {

        var value = "CreateTime";
        var equals = new Equals(value);

        assertTrue(equals.execute("CreateTime"));
    }

    @Test
    public void should_result_false_when_string_not_equals() {

        var value = "CreateTime";
        var equals = new Equals(value);

        assertFalse(equals.execute("LogAppendTime"));
    }

    @Test
    public void should_result_true_when_long_equals() {

        var value = 9223372036854775807L;
        var equals = new Equals(value);

        assertTrue(equals.execute(9223372036854775807L));
    }

    @Test
    public void should_result_false_when_long_not_equals() {

        var value = 9223372036854775807L;
        var equals = new Equals(value);

        assertFalse(equals.execute(4096L));
    }

    @Test
    public void should_result_true_when_double_equals() {

        var value = 0.0001D;
        var equals = new Equals(value);

        assertTrue(equals.execute(0.0001D));
    }

    @Test
    public void should_result_false_when_double_not_equals() {

        var value = 0.0001D;
        var equals = new Equals(value);

        assertFalse(equals.execute(0.5D));
    }

    @Test
    public void should_result_true_when_long_int_equals() {

        var operand = 92233L;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92233));
    }

    @Test
    public void should_result_false_when_long_int_not_equals() {

        var operand = 92233L;
        var equals = new Equals(operand);

        assertFalse(equals.execute(92234));
    }

    @Test
    public void should_result_true_when_long_short_equals() {

        var operand = 92L;
        var equals = new Equals(operand);

        assertTrue(equals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_long_short_not_equals() {


        var operand = 92L;
        var equals = new Equals(operand);

        assertFalse(equals.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_long_float_equals() {

        var operand = 92L;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92f));
    }

    @Test
    public void should_result_false_when_long_float_not_equals() {

        var operand = 92L;
        var equals = new Equals(operand);

        assertFalse(equals.execute(93f));
    }

    @Test
    public void should_result_true_when_long_double_equals() {

        var operand = 92L;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92D));
    }

    @Test
    public void should_result_false_when_long_double_not_equals() {

        var operand = 92L;
        var equals = new Equals(operand);

        assertFalse(equals.execute(93D));
    }

    @Test
    public void should_result_true_when_int_long_equals() {

        var operand = 92233;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92233L));
    }

    @Test
    public void should_result_false_when_int_long_not_equals() {

        var operand = 92233;
        var equals = new Equals(operand);

        assertFalse(equals.execute(92232L));
    }

    @Test
    public void should_result_true_when_int_short_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertTrue(equals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_int_short_not_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertFalse(equals.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_int_float_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92f));
    }

    @Test
    public void should_result_false_when_int_float_not_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertFalse(equals.execute(93f));
    }

    @Test
    public void should_result_true_when_int_double_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertTrue(equals.execute(92D));
    }

    @Test
    public void should_result_false_when_int_double_not_equals() {

        var operand = 92;
        var equals = new Equals(operand);

        assertFalse(equals.execute(93D));
    }

    @Test
    public void should_result_true_when_short_int_equals() {

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertTrue(equals.execute(92));
    }

    @Test
    public void should_result_false_whe_short_int_not_equals(){

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertFalse(equals.execute(93));

    }

    @Test
    public void should_result_true_when_short_long_equals() {

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertTrue(equals.execute(92L));
    }

    @Test
    public void should_result_false_whe_short_long_not_equals(){

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertFalse(equals.execute(93L));

    }

    @Test
    public void should_result_true_when_short_float_equals() {

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertTrue(equals.execute(92f));
    }

    @Test
    public void should_result_false_whe_short_float_not_equals(){

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertFalse(equals.execute(93f));

    }

    @Test
    public void should_result_true_when_short_double_equals() {

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertTrue(equals.execute(92D));
    }

    @Test
    public void should_result_false_when_short_double_not_equals(){

        var operand = Short.valueOf("92");
        var equals = new Equals(operand);

        assertFalse(equals.execute(93D));

    }

    @Test
    public void should_result_true_when_float_integer_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92));
    }

    @Test
    public void should_result_false_when_float_integer_not_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93));
    }

    @Test
    public void should_result_true_when_float_long_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92l));
    }

    @Test
    public void should_result_false_when_float_long_not_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93l));
    }

    @Test
    public void should_result_true_when_float_short_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertTrue(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_float_short_not_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertFalse(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_float_double_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92d));
    }

    @Test
    public void should_result_false_when_float_double_not_equals() {

        var operand = 92F;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93d));
    }

    @Test
    public void should_result_true_when_double_integer_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92));
    }

    @Test
    public void should_result_false_when_double_integer_not_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93));
    }

    @Test
    public void should_result_true_when_double_long_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92l));
    }

    @Test
    public void should_result_false_when_double_long_not_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93l));
    }

    @Test
    public void should_result_true_when_double_short_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertTrue(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_double_short_not_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertFalse(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_double_float_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertTrue(condition.execute(92f));
    }

    @Test
    public void should_result_false_when_double_float_not_equals() {

        var operand = 92d;
        var condition = new Equals(operand);

        assertFalse(condition.execute(93f));
    }

    @Test
    public void should_result_true_when_human_readable_time_equals() {

        var value = "2seconds";
        var equals = new Equals(value);

        assertTrue(equals.execute(2000l));
    }

    @Test
    public void should_to_string_result_human_readable_when_available() {

        var operand = "1day";
        var equals = new Equals(operand);

        assertEquals("==1day", equals.toString());
    }
}
