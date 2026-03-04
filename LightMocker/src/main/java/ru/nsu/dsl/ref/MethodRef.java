package ru.nsu.dsl.ref;
import java.io.Serializable;

public interface MethodRef<T, R> extends Serializable {
    R apply(T target);
}