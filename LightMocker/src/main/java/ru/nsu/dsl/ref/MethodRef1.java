package ru.nsu.dsl.ref;

import java.io.Serializable;

@FunctionalInterface
public interface MethodRef1<T, P1, R> extends Serializable {
    R apply(T target, P1 p1);
}