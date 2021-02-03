package com.github.kattlo.core.configuration.condition;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Test;

public class TextPatternTest {

    @Test
    public void should_throw_when_operand_is_not_string() {

        assertThrows(IllegalArgumentException.class, () -> new TextPattern(90));
    }

    @Test
    public void should_throw_when_operand_is_not_a_pattern() {

        assertThrows(PatternSyntaxException.class, () ->
            new TextPattern("[-"));
    }

    @Test
    public void should_throw_when_value_is_not_string() {

        var condition = new TextPattern(".*");

        assertThrows(IllegalArgumentException.class, () -> condition.execute(800));
    }

    @Test
    public void should_result_true_when_matches() {

        var condition = new TextPattern("[a-z\\-0-9]{1,255}");

        assertTrue(condition.execute("my-topic-0"));
    }

    @Test
    public void should_result_false_when_dot_not_match() {

        var condition = new TextPattern("[a-z\\-0-9]{1,255}");

        assertFalse(condition.execute("My-topic-0"));
    }
}
