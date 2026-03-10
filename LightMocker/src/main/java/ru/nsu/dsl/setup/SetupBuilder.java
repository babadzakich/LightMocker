package ru.nsu.dsl.setup;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;

import java.lang.reflect.Method;

public class SetupBuilder<T, R> {
    private final T mock;
    private final Method method;

    public SetupBuilder(T mock, Method method) {
        this.mock = mock;
        this.method = method;
    }

    // Если аргументы не указаны, создаем StubbingBuilder с пустым массивом
    public StubbingBuilder<T, R> withArgs(Object... args) {
        return new StubbingBuilder<>(mock, method, args);
    }

    // Позволяет пропустить .withArgs(), если метод без параметров
    public void returns(R value) {
        new StubbingBuilder<>(mock, method, new Object[0]).returns(value);
    }

    public void thenThrow(Throwable throwable) {
        new StubbingBuilder<>(mock, method, new Object[0]).thenThrow(throwable);
    }
}