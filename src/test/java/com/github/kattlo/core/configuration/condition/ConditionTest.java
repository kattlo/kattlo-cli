package com.github.kattlo.core.configuration.condition;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class ConditionTest {

    @Test
    public void should_throw_when_condition_is_invalid() {

        assertThrows(IllegalArgumentException.class, () ->
            Condition.of("*", ""));
    }

    @Test
    public void should_result_equals() {

        var actual = Condition.of("==", "");
        assertThat(actual, Matchers.instanceOf(Equals.class));
    }

    @Test
    public void should_result_not_equals() {

        var actual = Condition.of("!=", "");
        assertThat(actual, Matchers.instanceOf(NotEquals.class));
    }

    @Test
    public void should_result_greater() {

        var actual = Condition.of(">", 1000);
        assertThat(actual, Matchers.instanceOf(Greater.class));
    }

    @Test
    public void should_result_greater_or_equals() {

        var actual = Condition.of(">=", 3000);
        assertThat(actual, Matchers.instanceOf(GreaterOrEquals.class));
    }

    @Test
    public void should_result_less() {

        var actual = Condition.of("<", 200);
        assertThat(actual, Matchers.instanceOf(Less.class));
    }

    @Test
    public void should_result_less_or_equals() {

        var actual = Condition.of("<=", 400);
        assertThat(actual, Matchers.instanceOf(LessOrEquals.class));
    }

    @Test
    public void should_result_in() {

        var actual = Condition.of("in", List.of("a", "b", "c"));
        assertThat(actual, Matchers.instanceOf(In.class));
    }

    @Test
    public void should_result_not_in() {

        var actual = Condition.of("!IN", List.of("x", "y", "z"));
        assertThat(actual, Matchers.instanceOf(NotIn.class));
    }

    @Test
    public void should_result_text_pattern() {

        var actual = Condition.of("regex", "[a-z\\-0-9]{1,255}");
        assertThat(actual, Matchers.instanceOf(TextPattern.class));
    }
}
