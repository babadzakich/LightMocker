package ru.nsu.staticmock;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.model.StubRule;

import java.lang.reflect.Method;

/**
 * DSL builder for configuring static method stubs.
 *
 * <pre>
 *   CustomUltimateMocker.setupStatic(MyClass.class, "myMethod", int.class)
 *       .withArgs(42)
 *       .returns("hello");
 * </pre>
 */
public class StaticSetupBuilder<R> {

    private final Class<?> targetClass;
    private final Method method;
    private Object[] args = new Object[0];

    public StaticSetupBuilder(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    public StaticSetupBuilder<R> withArgs(Object... args) {
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
        StaticMockRegistry.registerRule(targetClass, rule);
    }
}

