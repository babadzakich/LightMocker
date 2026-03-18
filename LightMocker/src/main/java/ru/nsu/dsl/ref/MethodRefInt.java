package ru.nsu.dsl.ref;

import java.io.Serializable;

@FunctionalInterface
public interface MethodRefInt<T, R> extends Serializable {
    // ВАЖНО: используем Object, чтобы компилятор не спотыкался о примитивы
    R apply(T target, int p1);
}