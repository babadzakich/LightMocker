package ru.nsu.core.model;

import java.lang.reflect.Method;

public class Invocation {
    private Method method;
    private Object[] args;
    private long timestamp;

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
