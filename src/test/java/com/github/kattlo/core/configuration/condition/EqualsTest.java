package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EqualsTest {

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
