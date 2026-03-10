package ru.nsu.dsl.ref;
import java.io.Serializable;

@FunctionalInterface
public interface MethodRef<T, R> extends Serializable {
    R apply(T target);
}
