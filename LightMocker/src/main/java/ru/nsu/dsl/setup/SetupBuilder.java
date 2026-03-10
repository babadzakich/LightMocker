package ru.nsu.dsl.setup;

import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.answer.Answer;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.state.MockState;
import ru.nsu.dsl.ref.MethodRefExtractor;
import ru.nsu.exception.MockerException;

import java.io.Serializable;
import java.lang.reflect.Method;

public class SetupBuilder {
    private final Object mock;
    private final Method method;
    private Object[] args = new Object[0];

    // через method reference (0-арные методы)
    public SetupBuilder(Object mock, Serializable lambda) {
        this.mock = mock;
        this.method = MethodRefExtractor.extract(lambda);
    }

    // через имя метода + типы параметров (любые методы)
    public SetupBuilder(Object mock, String methodName, Class<?>... paramTypes) {
        this.mock = mock;
        this.method = resolveMethod(mock, methodName, paramTypes);
    }

    public SetupBuilder withArgs(Object... args) {
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
        // для ByteBuddy subclass — ищем в суперклассе тоже
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // также пробуем публичные методы интерфейсов
            }
            clazz = clazz.getSuperclass();
        }
        // пробуем через getMethod (ищет в интерфейсах)
        try {
            return mock.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new MockerException("Method not found: " + methodName
                    + " with params " + java.util.Arrays.toString(paramTypes)
                    + " on " + mock.getClass().getName());
        }
    }
}
