package com.github.kattlo.util;

import java.util.Objects;
import java.util.function.Supplier;

public class LazyLogging<T> {

    private Supplier<T> value;
    public LazyLogging(Supplier<T> value){
        this.value = Objects.requireNonNull(value);
    }

    public String toString() {

        System.out.print("===============================================================");
        var result = value.get();
        return (null!= result ? result.toString() : "");
    }

    public static <T> LazyLogging<T> of(Supplier<T> s) {
        return new LazyLogging<T>(s);
    }
}
