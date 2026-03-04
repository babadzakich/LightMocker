package ru.nsu.core.answer;

import ru.nsu.core.model.Invocation;
import ru.nsu.exception.MockerException;

public class ReturnsArgument<R> implements Answer<R> {
    private final int index;

    public ReturnsArgument(int index) {
        this.index = index;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R answer(Invocation invocation) {
        if (index < 0 || index >= invocation.getArgs().length) {
            throw new MockerException("Argument index " + index + " is out of bounds");
        }
        return (R) invocation.getArgs()[index];
    }
}
