package com.github.kattlo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NumberUtilTest {

    @Test
    public void should_throw_when_blank_string_arg() {

        assertThrows(IllegalArgumentException.class,
            () -> NumberUtil.fromHumanReadable(" "));
    }

    @Test
    public void should_result_even_has_space_between_value_and_symbol() {

        var expected = 0.5d;

        var actual = NumberUtil.fromHumanReadable("50 %");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_double_when_percent_symbol() {

        var expected = 0.01d;

        var actual = NumberUtil.fromHumanReadable("1%");

        assertEquals(expected, actual);
    }

    @Test
    public void should_throw_when_cant_parse_math_as_double() {

        assertThrows(NumberFormatException.class,
            () -> NumberUtil.fromHumanReadable("kk%"));
    }

    @Test
    public void should_throw_when_cant_parse_bytes_as_long() {

        assertThrows(NumberFormatException.class,
            () -> NumberUtil.fromHumanReadable("kkGiB"));
    }

    @Test
    public void should_throw_to_bytes_symbol_it_is_not_expected(){
        assertThrows(IllegalArgumentException.class,
            () -> NumberUtil.fromHumanReadable("40TiB"));
    }

    @Test
    public void should_result_long_number_of_bytes_when_gib_symbol() {

        var expected = (long)(1 * 1024 * 1024 * 1024);

        var actual = NumberUtil.fromHumanReadable("1GiB");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_long_number_of_bytes_when_mib_symbol() {

        var expected = (long)(1 * 1024 * 1024);

        var actual = NumberUtil.fromHumanReadable("1MiB");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_long_number_of_bytes_when_kib_symbol() {

        var expected = 1 * 1024l;

        var actual = NumberUtil.fromHumanReadable("1KiB");

        assertEquals(expected, actual);
    }

    @Test
    public void should_throw_when_cant_parse_time_as_long() {

        assertThrows(IllegalArgumentException.class,
            () -> NumberUtil.fromHumanReadable("dday"));
    }

    @Test
    public void should_result_millis_when_day() {

        var expected = 1 * 24 * 60 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("1day");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_days() {

        var expected = 3 * 24 * 60 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("3days");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_hour() {

        var expected = 1 * 60 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("1hour");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_hours() {

        var expected = 10 * 60 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("10hours");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_minute() {

        var expected = 1 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("1minute");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_minutes() {

        var expected = 5 * 60 * 1000l;

        var actual = NumberUtil.fromHumanReadable("5minutes");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_second() {

        var expected = 1 * 1000l;

        var actual = NumberUtil.fromHumanReadable("1second");

        assertEquals(expected, actual);
    }

    @Test
    public void should_result_millis_when_seconds() {

        var expected = 6 * 1000l;

        var actual = NumberUtil.fromHumanReadable("6seconds");

        assertEquals(expected, actual);
    }
}
