package ru.nsu.dsl.setup;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.registry.StubRegistry;

import java.lang.reflect.Method;


public class StubbingBuilder<T, R> {
    private final T mock;
    private final Method method;
    private final Object[] args;
    public StubbingBuilder(T mock, Method method, Object[] args) {
        this.mock = mock;
        this.method = method;
        this.args = args;
    }

    public void returns(Object value) {
        answers(invocation -> (R)value);
    }

    public void thenThrow(Throwable throwable) {
        answers(invocation -> { throw throwable; });
    }

    public void answers(Answer<R> answer) {
        // Создаем финальное правило
        StubRule rule = new StubRule(method, args, answer);

        // Нам нужен объект Invocation для матчинга
        Invocation inv = new Invocation(method, args);

        // Сохраняем в реестр
        StubRegistry.registerRule(mock, rule);
    }
}