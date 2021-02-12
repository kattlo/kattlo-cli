package com.github.kattlo.util;

import java.util.Objects;
import java.util.Optional;

/**
 * @author fabiojose
 */
public class MachineReadableSupport {

    private static final String NO_READABLE = null;

    private final Optional<String> readable;
    private final Object machine;

    private MachineReadableSupport(String readable, Object machine){
        this.readable = Optional.ofNullable(readable);
        this.machine = machine;
    }

    public static MachineReadableSupport of(Object value) {
        Objects.requireNonNull(value, "provide a non-null value argument");

        MachineReadableSupport result;
        if(value instanceof String
            && NumberUtil.isHumanReadableCandidate((String)value)){

            result = new MachineReadableSupport((String)value,
                NumberUtil.fromHumanReadable((String)value));

        } else {
            result = new MachineReadableSupport(NO_READABLE, value);
        }

        return result;
    }

    public Optional<String> getHumanReadable() {
        return readable;
    }

    public Object getMachineReadable() {
        return machine;
    }
}
