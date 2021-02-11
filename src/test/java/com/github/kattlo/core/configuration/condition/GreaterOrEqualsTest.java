package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GreaterOrEqualsTest {

    @Test
    public void should_throw_when_value_is_not_a_number() {

        var greaterOrEquals = new GreaterOrEquals(10);
        assertThrows(IllegalArgumentException.class, () ->
            greaterOrEquals.execute("10"));
    }

    @Test
    public void should_throw_when_operand_is_not_a_number() {

        assertThrows(IllegalArgumentException.class, () -> new GreaterOrEquals("10"));
    }

    @Test
    public void should_result_true_when_long_long_greaterOrEquals() {

        var operand = 9223372036854775806L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(9223372036854775807L));
    }

    @Test
    public void should_result_false_when_long_long_not_greaterOrEquals() {

        var operand = 9223372036854775807L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(9223372036854775806L));
    }

    @Test
    public void should_result_true_when_long_int_greaterOrEquals() {

        var operand = 92233L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(92234));
    }

    @Test
    public void should_result_false_when_long_int_not_greaterOrEquals() {

        var operand = 92233L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(92232));
    }

    @Test
    public void should_result_true_when_long_short_greaterOrEquals() {

        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_long_short_not_greaterOrEquals() {


        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_long_float_greaterOrEquals() {

        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(92f));
    }

    @Test
    public void should_result_false_when_long_float_not_greaterOrEquals() {

        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91f));
    }

    @Test
    public void should_result_true_when_long_double_greaterOrEquals() {

        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93D));
    }

    @Test
    public void should_result_false_when_long_double_not_greaterOrEquals() {

        var operand = 92L;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91D));
    }

    @Test
    public void should_result_true_when_int_int_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93));
    }

    @Test
    public void should_result_false_whe_int_int_not_greaterOrEquals(){

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91));

    }

    @Test
    public void should_result_true_when_int_long_greaterOrEquals() {

        var operand = 92233;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(92234L));
    }

    @Test
    public void should_result_false_when_int_long_not_greaterOrEquals() {

        var operand = 92233;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(92232L));
    }

    @Test
    public void should_result_true_when_int_short_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_int_short_not_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_int_float_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(92f));
    }

    @Test
    public void should_result_false_when_int_float_not_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91f));
    }

    @Test
    public void should_result_true_when_int_double_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93D));
    }

    @Test
    public void should_result_false_when_int_double_not_greaterOrEquals() {

        var operand = 92;
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91D));
    }

    @Test
    public void should_result_true_when_short_short_greaterOrEquals() {

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_whe_short_short_not_greaterOrEquals(){

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(Short.valueOf("91")));

    }

    @Test
    public void should_result_true_when_short_int_greaterOrEquals() {

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93));
    }

    @Test
    public void should_result_false_whe_short_int_not_greaterOrEquals(){

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91));

    }

    @Test
    public void should_result_true_when_short_long_greaterOrEquals() {

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93L));
    }

    @Test
    public void should_result_false_whe_short_long_not_greaterOrEquals(){

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91L));

    }

    @Test
    public void should_result_true_when_short_float_greaterOrEquals() {

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93f));
    }

    @Test
    public void should_result_false_whe_short_float_not_greaterOrEquals(){

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91f));

    }

    @Test
    public void should_result_true_when_short_double_greaterOrEquals() {

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertTrue(greaterOrEquals.execute(93D));
    }

    @Test
    public void should_result_false_when_short_double_not_greaterOrEquals(){

        var operand = Short.valueOf("92");
        var greaterOrEquals = new GreaterOrEquals(operand);

        assertFalse(greaterOrEquals.execute(91D));

    }

    @Test
    public void should_result_true_when_float_float_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93F));
    }

    @Test
    public void should_result_false_when_float_float_not_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91F));
    }

    @Test
    public void should_result_true_when_float_integer_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93));
    }

    @Test
    public void should_result_false_when_float_integer_not_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91));
    }

    @Test
    public void should_result_true_when_float_long_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93l));
    }

    @Test
    public void should_result_false_when_float_long_not_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91l));
    }

    @Test
    public void should_result_true_when_float_short_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_false_when_float_short_not_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_float_double_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93d));
    }

    @Test
    public void should_result_false_when_float_double_not_greaterOrEquals() {

        var operand = 92F;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91d));
    }

    @Test
    public void should_result_true_when_double_double_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93d));
    }

    @Test
    public void should_result_false_when_double_double_not_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91d));
    }

    @Test
    public void should_result_true_when_double_integer_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(92));
    }

    @Test
    public void should_result_false_when_double_integer_not_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91));
    }

    @Test
    public void should_result_true_when_double_long_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93l));
    }

    @Test
    public void should_result_false_when_double_long_not_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91l));
    }

    @Test
    public void should_result_true_when_double_short_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_when_double_short_not_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_double_float_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(93f));
    }

    @Test
    public void should_result_false_when_double_float_not_greaterOrEquals() {

        var operand = 92d;
        var condition = new GreaterOrEquals(operand);

        assertFalse(condition.execute(91f));
    }

    @Test
    public void should_result_true_when_human_readable() {

        var operand = "2hours";
        var condition = new GreaterOrEquals(operand);

        assertTrue(condition.execute(2 * 60 * 60 * 1000l));
    }

    @Test
    public void should_to_string_result_human_readable_when_available() {

        var operand = "1GiB";
        var condition = new GreaterOrEquals(operand);

        assertEquals(">=1GiB", condition.toString());
    }
}
