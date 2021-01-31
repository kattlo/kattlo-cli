package com.github.kattlo.core.configuration.rule;

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
}
