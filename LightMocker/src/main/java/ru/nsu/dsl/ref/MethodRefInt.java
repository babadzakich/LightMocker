package ru.nsu.dsl.ref;

import java.io.Serializable;

@FunctionalInterface
public interface MethodRefInt<T, R> extends Serializable {
    R apply(T target, int p1);
}