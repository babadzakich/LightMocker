package ru.nsu.staticmock;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Scope-based static mock. No Java agent, no bytecode rewriting.
 * All calls go through {@link #invoke(String, Object...)}.
 *
 * <pre>
 *   try (var mock = StaticMock.mock(MathUtils.class)) {
 *       mock.setup("add", int.class, int.class).withArgs(3, 4).returns(100);
 *       int r = mock.invoke("add", 3, 4); // 100
 *   }
 * </pre>
 */
public class StaticMock implements AutoCloseable {

    private final Class<?> targetClass;

    private StaticMock(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public static StaticMock mock(Class<?> clazz) {
        return new StaticMock(clazz);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Creates a setup builder for configuring the static method stub.
     */
    public <R> StaticSetupBuilder<R> setup(String methodName, Class<?>... paramTypes) {
        try {
            Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
            return new StaticSetupBuilder<>(targetClass, method);
        } catch (NoSuchMethodException e) {
            throw new ru.nsu.exception.MockerException(
                    "Static method not found: " + methodName + " on " + targetClass.getName());
        }
    }

    /**
     * Invokes a static method through the mock.
     * If a matching stub exists — returns the stubbed value.
     * Otherwise — calls the real method.
     */
    @SuppressWarnings("unchecked")
    public <R> R invoke(String methodName, Object... args) throws Throwable {
        Object[] safeArgs = args != null ? args : new Object[0];
        Method method = resolveMethod(methodName, safeArgs);

        Invocation invocation = new Invocation(targetClass, method, safeArgs);
        StaticMockRegistry.registerInvocation(targetClass, invocation);

        Optional<StubRule> rule = StaticMockRegistry.findMatchingRule(targetClass, invocation);
        if (rule.isPresent()) {
            return (R) rule.get().getAnswer().answer(invocation);
        }

        // No stub → call real method
        method.setAccessible(true);
        return (R) method.invoke(null, safeArgs);
    }

    @Override
    public void close() {
        StaticMockRegistry.clear(targetClass);
    }

    private Method resolveMethod(String methodName, Object[] args) {
        for (Method m : targetClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                return m;
            }
        }
        throw new ru.nsu.exception.MockerException(
                "Static method not found: " + methodName
                        + " with " + args.length + " args on " + targetClass.getName());
    }
}
