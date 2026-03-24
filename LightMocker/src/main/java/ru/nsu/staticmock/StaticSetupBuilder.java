package ru.nsu.staticmock;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.util.MethodUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
    private Method method;
    private final String methodName;
    private final List<Method> candidates;
    private Object[] args = new Object[0];

    public StaticSetupBuilder(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
        this.methodName = method.getName();
        this.candidates = null;
    }

    public StaticSetupBuilder(Class<?> targetClass, String methodName, List<Method> candidates) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.candidates = candidates;
        if (candidates.size() == 1) {
            this.method = candidates.get(0);
        } else {
            this.method = null;
        }
    }

    public StaticSetupBuilder<R> withArgs(Object... args) {
        this.args = args;
        if (method == null) {
            resolveMethodByArgs();
        }
        return this;
    }

    public void returns(Object value) {
        ensureMethodResolved();
        register(new Returns<>(value));
    }

    public void answers(Answer<?> answer) {
        ensureMethodResolved();
        register(answer);
    }

    public void thenThrow(Throwable throwable) {
        ensureMethodResolved();
        register(new ThrowsException(throwable));
    }

    private void ensureMethodResolved() {
        if (method == null) {
            resolveMethodByArgs();
        }
    }

// ...existing code...
    private void resolveMethodByArgs() {
        if (candidates == null) {
            throw new ru.nsu.exception.MockerException("Validation failed: no method candidates to resolve from.");
        }

        List<Method> matched = candidates.stream()
                .filter(m -> MethodUtils.isCompatible(m, args))
                .toList();

        if (matched.isEmpty()) {
            throw new ru.nsu.exception.MockerException(
                    "No method found for " + methodName + " with args " + Arrays.toString(args));
        }
        if (matched.size() > 1) {
            // Try to find exact match if multiple compatible (e.g. overloads with inheritance)
            // But for now keeping it simple - if ambiguity remains, throw.
            throw new ru.nsu.exception.MockerException(
                    "Ambiguous method call for " + methodName + " with args " + Arrays.toString(args) +
                            ". Candidates: " + matched);
        }
        this.method = matched.get(0);
    }

    private void register(Answer<?> answer) {
        StubRule rule = new StubRule(method, args, answer);
        StaticMockRegistry.registerRule(targetClass, rule);
    }
}

