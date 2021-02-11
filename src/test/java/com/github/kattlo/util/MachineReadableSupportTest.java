package com.github.kattlo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MachineReadableSupportTest {

    @Test
    public void should_has_human_readable() {

        var actual = MachineReadableSupport.of("1KiB");

        assertEquals("1KiB", actual.getHumanReadable().get());
        assertEquals(1024l, actual.getMachineReadable());
    }

    @Test
    public void should_has_just_machine_readable() {

        var actual = MachineReadableSupport.of(1024);

        assertTrue(actual.getHumanReadable().isEmpty());
        assertEquals(1024, actual.getMachineReadable());
    }
}
