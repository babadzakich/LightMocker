package ru.nsu.core.answer;

import ru.nsu.core.model.Invocation;

public interface Answer<R> {

    R answer(Invocation invocation) throws Throwable;
}
