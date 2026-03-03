package ru.nsu.core.answer;

import ru.nsu.core.model.Invocation;


public class ThrowsException implements Answer<Object> {
    private final Throwable throwable;

    public ThrowsException(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Object answer(Invocation invocation) throws Throwable {
        throw throwable;
    }
}
