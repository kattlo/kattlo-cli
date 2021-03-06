package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GreaterTest {


    @Test
    public void should_throw_when_value_is_not_a_number() {

        var greater = new Greater(10);
        assertThrows(IllegalArgumentException.class, () ->
            greater.execute("10"));
    }

    @Test
    public void should_throw_when_operand_is_not_a_number() {

        assertThrows(IllegalArgumentException.class, () -> new Greater("10"));
    }

    @Test
    public void should_result_true_when_long_long_greater() {

        var operand = 9223372036854775806L;
        var greater = new Greater(operand);

        assertTrue(greater.execute(9223372036854775807L));
    }

    @Test
    public void should_result_false_when_long_long_not_greater() {

        var operand = 9223372036854775807L;
        var greater = new Greater(operand);

        assertFalse(greater.execute(9223372036854775807L));
    }

    @Test
    public void should_result_true_when_long_int_greater() {

        var operand = 92233L;
        var greater = new Greater(operand);

        assertTrue(greater.execute(92234));
    }

    @Test
    public void should_result_false_when_long_int_not_greater() {

        var operand = 92233L;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92233));
    }

    @Test
    public void should_result_true_when_long_short_greater() {

        var operand = 92L;
        var greater = new Greater(operand);

        assertTrue(greater.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_when_long_short_not_greater() {


        var operand = 92L;
        var greater = new Greater(operand);

        assertFalse(greater.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_true_when_long_float_greater() {

        var operand = 92L;
        var greater = new Greater(operand);

        assertTrue(greater.execute(93f));
    }

    @Test
    public void should_result_false_when_long_float_not_greater() {

        var operand = 92L;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92f));
    }

    @Test
    public void should_result_true_when_long_double_greater() {

        var operand = 92L;
        var greater = new Greater(operand);

        assertTrue(greater.execute(93D));
    }

    @Test
    public void should_result_false_when_long_double_not_greater() {

        var operand = 92L;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92D));
    }

    @Test
    public void should_result_true_when_int_int_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertTrue(greater.execute(93));
    }

    @Test
    public void should_result_false_whe_int_int_not_greater(){

        var operand = 92;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92));

    }

    @Test
    public void should_result_true_when_int_long_greater() {

        var operand = 92233;
        var greater = new Greater(operand);

        assertTrue(greater.execute(92234L));
    }

    @Test
    public void should_result_false_when_int_long_not_greater() {

        var operand = 92233;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92233L));
    }

    @Test
    public void should_result_true_when_int_short_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertTrue(greater.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_when_int_short_not_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertFalse(greater.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_true_when_int_float_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertTrue(greater.execute(93f));
    }

    @Test
    public void should_result_false_when_int_float_not_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92f));
    }

    @Test
    public void should_result_true_when_int_double_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertTrue(greater.execute(93D));
    }

    @Test
    public void should_result_false_when_int_double_not_greater() {

        var operand = 92;
        var greater = new Greater(operand);

        assertFalse(greater.execute(92D));
    }

    @Test
    public void should_result_true_when_short_short_greater() {

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertTrue(greater.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_whe_short_short_not_greater(){

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertFalse(greater.execute(Short.valueOf("92")));

    }

    @Test
    public void should_result_true_when_short_int_greater() {

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertTrue(greater.execute(93));
    }

    @Test
    public void should_result_false_whe_short_int_not_greater(){

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertFalse(greater.execute(92));

    }

    @Test
    public void should_result_true_when_short_long_greater() {

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertTrue(greater.execute(93L));
    }

    @Test
    public void should_result_false_whe_short_long_not_greater(){

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertFalse(greater.execute(92L));

    }

    @Test
    public void should_result_true_when_short_float_greater() {

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertTrue(greater.execute(93f));
    }

    @Test
    public void should_result_false_whe_short_float_greater(){

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertFalse(greater.execute(92f));

    }

    @Test
    public void should_result_true_when_short_double_greater() {

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertTrue(greater.execute(93D));
    }

    @Test
    public void should_result_false_when_short_double_not_greater(){

        var operand = Short.valueOf("92");
        var greater = new Greater(operand);

        assertFalse(greater.execute(92D));

    }

    @Test
    public void should_result_true_when_float_float_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93F));
    }

    @Test
    public void should_result_false_when_float_float_not_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92F));
    }

    @Test
    public void should_result_true_when_float_integer_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93));
    }

    @Test
    public void should_result_false_when_float_integer_not_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92));
    }

    @Test
    public void should_result_true_when_float_long_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93l));
    }

    @Test
    public void should_result_false_when_float_long_not_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92l));
    }

    @Test
    public void should_result_true_when_float_short_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertTrue(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_when_float_short_not_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertFalse(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_true_when_float_double_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93d));
    }

    @Test
    public void should_result_false_when_float_double_not_greater() {

        var operand = 92F;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92d));
    }

    @Test
    public void should_result_true_when_double_double_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93d));
    }

    @Test
    public void should_result_false_when_double_double_not_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92d));
    }

    @Test
    public void should_result_true_when_double_integer_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93));
    }

    @Test
    public void should_result_false_when_double_integer_not_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92));
    }

    @Test
    public void should_result_true_when_double_long_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93l));
    }

    @Test
    public void should_result_false_when_double_long_not_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92l));
    }

    @Test
    public void should_result_true_when_double_short_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertTrue(condition.execute(Short.valueOf("93")));
    }

    @Test
    public void should_result_false_when_double_short_not_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertFalse(condition.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_true_when_double_float_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertTrue(condition.execute(93f));
    }

    @Test
    public void should_result_false_when_double_float_not_greater() {

        var operand = 92d;
        var condition = new Greater(operand);

        assertFalse(condition.execute(92f));
    }

    @Test
    public void should_result_true_when_human_readable() {

        var operand = "2hours";
        var condition = new Greater(operand);

        assertTrue(condition.execute(3 * 60 * 60 * 1000l));
    }

    @Test
    public void should_to_string_result_human_readable_when_available() {

        var operand = "10%";
        var condition = new Greater(operand);

        assertEquals(">10%", condition.toString());
    }
}
