package ru.nsu.dsl.setup;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.registry.StubRegistry;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;

public class SetupBuilder<R> {
    private final Object mock;
    private final Method method;
    private Object[] args = new Object[0];

    public SetupBuilder(Object mock, String methodName, Class<?>... paramTypes) {
        this.mock = mock;
        this.method = resolveMethod(mock, methodName, paramTypes);
    }

    public SetupBuilder<R> withArgs(Object... args) {
        this.args = args;
        return this;
    }

    public void returns(Object value) {
        register(new Returns<>(value));
    }

    public void answers(Answer<?> answer) {
        register(answer);
    }

    public void thenThrow(Throwable throwable) {
        register(new ThrowsException(throwable));
    }

    private void register(Answer<?> answer) {
        StubRule rule = new StubRule(method, args, answer);
        MockState.getInstance().getStubRegistry().registerRule(mock, rule);
    }

    private static Method resolveMethod(Object mock, String methodName, Class<?>... paramTypes) {
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // продолжаем вверх по иерархии
            }
            clazz = clazz.getSuperclass();
        }
        try {
            return mock.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new MockerException("Method not found: " + methodName
                    + " with params " + java.util.Arrays.toString(paramTypes)
                    + " on " + mock.getClass().getName());
        }
    }
}