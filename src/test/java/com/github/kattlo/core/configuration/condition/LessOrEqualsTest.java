package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LessOrEqualsTest {

    @Test
    public void should_throw_when_value_is_not_a_number() {

        var lessOrEquals = new LessOrEquals(10);
        assertThrows(IllegalArgumentException.class, () ->
            lessOrEquals.execute("10"));
    }

    @Test
    public void should_throw_when_operand_is_not_a_number() {

        assertThrows(IllegalArgumentException.class, () -> new LessOrEquals("10"));
    }

    @Test
    public void should_result_true_when_long_long_lessOrEquals() {

        var operand = 9223372036854775806L;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(9223372036854775806L));
    }

    @Test
    public void should_result_false_when_long_long_not_lessOrEquals() {

        var operand = 9223372036854775806L;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(9223372036854775807L));
    }

    @Test
    public void should_result_true_when_long_int_lessOrEquals() {

        var operand = 92233L;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(92233));
    }

    @Test
    public void should_result_false_when_long_int_not_lessOrEquals() {

        var operand = 92233L;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(92234));
    }

    @Test
    public void should_result_true_when_long_short_lessOrEquals() {

        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_false_when_long_short_not_lessOrEquals() {


        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_long_float_lessOrEquals() {

        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91f));
    }

    @Test
    public void should_result_false_when_long_float_not_lessOrEquals() {

        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93f));
    }

    @Test
    public void should_result_true_when_long_double_lessOrEquals() {

        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91D));
    }

    @Test
    public void should_result_false_when_long_double_not_lessOrEquals() {

        var operand = 92L;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93D));
    }

    @Test
    public void should_result_true_when_int_int_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(92));
    }

    @Test
    public void should_result_false_whe_int_int_not_lessOrEquals(){

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93));

    }

    @Test
    public void should_result_true_when_int_long_lessOrEquals() {

        var operand = 92233;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(92232L));
    }

    @Test
    public void should_result_false_when_int_long_not_lessOrEquals() {

        var operand = 92233;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(92234L));
    }

    @Test
    public void should_result_true_when_int_short_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_int_short_not_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_int_float_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91f));
    }

    @Test
    public void should_result_false_when_int_float_not_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93f));
    }

    @Test
    public void should_result_true_when_int_double_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91D));
    }

    @Test
    public void should_result_false_when_int_double_not_lessOrEquals() {

        var operand = 92;
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93D));
    }

    @Test
    public void should_result_true_when_short_short_lessOrEquals() {

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_whe_short_short_not_lessOrEquals(){

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(Short.valueOf("93")));

    }

    @Test
    public void should_result_true_when_short_int_lessOrEquals() {

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91));
    }

    @Test
    public void should_result_false_whe_short_int_not_lessOrEquals(){

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93));

    }

    @Test
    public void should_result_true_when_short_long_lessOrEquals() {

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(91L));
    }

    @Test
    public void should_result_false_whe_short_long_not_lessOrEquals(){

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93L));

    }

    @Test
    public void should_result_true_when_short_float_lessOrEquals() {

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(92f));
    }

    @Test
    public void should_result_false_whe_short_float_not_lessOrEquals(){

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93f));

    }

    @Test
    public void should_result_true_when_short_double_lessOrEquals() {

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertTrue(lessOrEquals.execute(92D));
    }

    @Test
    public void should_result_false_when_short_double_not_lessOrEquals(){

        var operand = Short.valueOf("92");
        var lessOrEquals = new LessOrEquals(operand);

        assertFalse(lessOrEquals.execute(93D));

    }

    @Test
    public void should_result_true_when_float_float_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91F));
    }

    @Test
    public void should_result_false_when_float_float_not_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93F));
    }

    @Test
    public void should_result_true_when_float_integer_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91));
    }

    @Test
    public void should_result_false_when_float_integer_not_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93));
    }

    @Test
    public void should_result_true_when_float_long_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91l));
    }

    @Test
    public void should_result_false_when_float_long_not_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93l));
    }

    @Test
    public void should_result_true_when_float_short_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_float_short_not_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_float_double_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91d));
    }

    @Test
    public void should_result_false_when_float_double_not_lessOrEquals() {

        var operand = 92F;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93d));
    }

    @Test
    public void should_result_true_when_double_double_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91d));
    }

    @Test
    public void should_result_false_when_double_double_not_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93d));
    }

    @Test
    public void should_result_true_when_double_integer_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91));
    }

    @Test
    public void should_result_false_when_double_integer_not_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93));
    }

    @Test
    public void should_result_true_when_double_long_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(92l));
    }

    @Test
    public void should_result_false_when_double_long_not_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93l));
    }

    @Test
    public void should_result_true_when_double_short_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_false_when_double_short_not_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_true_when_double_float_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(91f));
    }

    @Test
    public void should_result_false_when_double_float_not_lessOrEquals() {

        var operand = 92d;
        var condition = new LessOrEquals(operand);

        assertFalse(condition.execute(93f));
    }

    @Test
    public void should_result_true_when_human_readable() {

        var operand = "2hours";
        var condition = new LessOrEquals(operand);

        assertTrue(condition.execute(2 * 60 * 60 * 1000l));
    }

    @Test
    public void should_to_string_result_human_readable_when_available() {

        var operand = "60MiB";
        var condition = new LessOrEquals(operand);

        assertEquals("<=60MiB", condition.toString());
    }
}
