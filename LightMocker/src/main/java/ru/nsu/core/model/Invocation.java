package ru.nsu.core.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * Represent a single method invocation on a mock object.
 */
@Getter
@Setter
public class Invocation {
    private final Object mockInstance;
    private final Method method;
    private final Object[] args;
    private final long timestamp;

    public Invocation(Object mockInstance, Method method, Object[] args) {
        this.mockInstance = mockInstance;
        this.method = method;
        this.args = args != null ? args : new Object[0];
        this.timestamp = System.nanoTime();
    }


}
