package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class InTest {

    @Test
    public void should_throw_when_operand_is_not_a_list() {

        assertThrows(IllegalArgumentException.class, () ->
            new In("operand"));
    }

    @Test
    public void should_result_true_when_contains_string() {

        var operand = List.of(
            "lz4",
            "snappy"
        );

        var condition = new In(operand);

        assertTrue(condition.execute("lz4"));
    }

    @Test
    public void should_result_true_when_contains_short() {

        var operand = List.of(
            Short.valueOf("90"),
            Short.valueOf("92")
        );

        var condition = new In(operand);

        assertTrue(condition.execute(Short.valueOf("90")));
    }

    @Test
    public void should_result_true_when_contains_integer() {

        var operand = List.of(200, 300);

        var condition = new In(operand);

        assertTrue(condition.execute(300));
    }

    @Test
    public void should_result_true_when_contains_long() {

        var operand = List.of(200L, 300L);

        var condition = new In(operand);

        assertTrue(condition.execute(300L));
    }

    @Test
    public void should_result_true_when_contains_float() {

        var operand = List.of(200.6f, 300.9f);

        var condition = new In(operand);

        assertTrue(condition.execute(300.9f));
    }

    @Test
    public void should_result_true_when_contains_double() {

        var operand = List.of(200.6d, 300.9d);

        var condition = new In(operand);

        assertTrue(condition.execute(300.9d));
    }

    @Test
    public void should_result_true_when_contains_boolean() {

        var operand = List.of(true, false);

        var condition = new In(operand);

        assertTrue(condition.execute(false));
    }
}
