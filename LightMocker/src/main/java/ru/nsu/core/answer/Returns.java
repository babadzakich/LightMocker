package ru.nsu.core.answer;

import ru.nsu.core.model.Invocation;

public class Returns<R> implements Answer<R> {
    private final R value;

    public Returns(R value) {
        this.value = value;
    }

    @Override
    public R answer(Invocation invocation) {
        return value;
    }
}