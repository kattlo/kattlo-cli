package com.github.kattlo.core.configuration.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LessTest {

    @Test
    public void should_throw_when_value_is_not_a_number() {

        var less = new Less(10);
        assertThrows(IllegalArgumentException.class, () ->
            less.execute("10"));
    }

    @Test
    public void should_throw_when_operand_is_not_a_number() {

        assertThrows(IllegalArgumentException.class, () -> new Less("10"));
    }

    @Test
    public void should_result_true_when_long_long_less() {

        var operand = 9223372036854775806L;
        var less = new Less(operand);

        assertTrue(less.execute(9223372036854775805L));
    }

    @Test
    public void should_result_false_when_long_long_not_less() {

        var operand = 9223372036854775807L;
        var less = new Less(operand);

        assertFalse(less.execute(9223372036854775807L));
    }

    @Test
    public void should_result_true_when_long_int_less() {

        var operand = 92233L;
        var less = new Less(operand);

        assertTrue(less.execute(92232));
    }

    @Test
    public void should_result_false_when_long_int_not_less() {

        var operand = 92233L;
        var less = new Less(operand);

        assertFalse(less.execute(92233));
    }

    @Test
    public void should_result_true_when_long_short_less() {

        var operand = 92L;
        var less = new Less(operand);

        assertTrue(less.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_false_when_long_short_not_less() {


        var operand = 92L;
        var less = new Less(operand);

        assertFalse(less.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_true_when_int_int_less() {

        var operand = 92;
        var less = new Less(operand);

        assertTrue(less.execute(91));
    }

    @Test
    public void should_result_false_whe_int_int_not_less(){

        var operand = 92;
        var less = new Less(operand);

        assertFalse(less.execute(92));

    }

    @Test
    public void should_result_true_when_int_long_less() {

        var operand = 92233;
        var less = new Less(operand);

        assertTrue(less.execute(92232L));
    }

    @Test
    public void should_result_false_when_int_long_not_less() {

        var operand = 92233;
        var less = new Less(operand);

        assertFalse(less.execute(92233L));
    }

    @Test
    public void should_result_true_when_int_short_less() {

        var operand = 92;
        var less = new Less(operand);

        assertTrue(less.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_false_when_int_short_not_less() {

        var operand = 92;
        var less = new Less(operand);

        assertFalse(less.execute(Short.valueOf("92")));
    }

    @Test
    public void should_result_true_when_short_short_less() {

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertTrue(less.execute(Short.valueOf("91")));
    }

    @Test
    public void should_result_false_whe_short_short_not_less(){

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertFalse(less.execute(Short.valueOf("92")));

    }

    @Test
    public void should_result_true_when_short_int_less() {

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertTrue(less.execute(91));
    }

    @Test
    public void should_result_false_whe_short_int_not_less(){

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertFalse(less.execute(92));

    }

    @Test
    public void should_result_true_when_short_long_less() {

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertTrue(less.execute(91L));
    }

    @Test
    public void should_result_false_whe_short_long_not_less(){

        var operand = Short.valueOf("92");
        var less = new Less(operand);

        assertFalse(less.execute(92L));

    }
}
