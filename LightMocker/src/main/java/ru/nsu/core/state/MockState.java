package ru.nsu.core.state;

import ru.nsu.core.model.Invocation;

public class MockState {

    public static final ThreadLocal<Invocation> lastInvocation = new ThreadLocal<>();

    public static final ThreadLocal<Object> lastMock = new ThreadLocal<>();

    public static void clear() {
        lastInvocation.remove();
        lastMock.remove();
    }
}
