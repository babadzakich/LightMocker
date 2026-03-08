package ru.nsu.core.model;

import java.lang.reflect.Method;

/**
 * Represent a single method invocation on a mock object.
 */
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

    public Object getMockInstance() {
        return mockInstance;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
