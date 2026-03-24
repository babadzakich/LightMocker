package ru.nsu.dsl.setup;

import ru.nsu.core.answer.Answer;
import ru.nsu.core.answer.Returns;
import ru.nsu.core.answer.ThrowsException;
import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;
    import ru.nsu.core.util.MethodUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetupBuilder<R> {
    private final Object mock;
    private final String methodName;
    private Method method;
    private final List<Method> candidates;
    private Object[] args = new Object[0];

    public SetupBuilder(Object mock, String methodName, Class<?>... paramTypes) {
        this.mock = mock;
        this.methodName = methodName;

        if (paramTypes != null && paramTypes.length > 0) {
            this.method = resolveMethodStrict(mock, methodName, paramTypes);
            this.candidates = null;
        } else {
            this.candidates = collectCandidates(mock, methodName);
            if (candidates.isEmpty()) {
                throw new MockerException("Method not found: " + methodName + " on " + mock.getClass().getName());
            }
            this.method = null;
        }
    }

    public SetupBuilder(Object mock, Invocation invocation) {
        this.mock = mock;
        this.method = invocation.getMethod();
        this.args = invocation.getArgs();
        methodName = invocation.getMethod().getName();
        candidates = collectCandidates(mock, methodName);
    }

    public SetupBuilder<R> withArgs(Object... args) {
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

    private void resolveMethodByArgs() {
        List<Method> compatible = candidates.stream()
                .filter(m -> MethodUtils.isCompatible(m, args))
                .toList();

        if (compatible.isEmpty()) {
             throw new MockerException(
                    "No method found for " + methodName + " with args " + Arrays.deepToString(args) +
                    " on " + mock.getClass().getName());
        }

        // De-duplicate by signature (name + parameter types), keeping the first (most specific) one.
        List<Method> uniqueSignatures = new ArrayList<>();
        for (Method m : compatible) {
            boolean duplicate = false;
            for (Method existing : uniqueSignatures) {
                if (Arrays.equals(m.getParameterTypes(), existing.getParameterTypes())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                uniqueSignatures.add(m);
            }
        }

        if (uniqueSignatures.size() > 1) {
             throw new MockerException(
                    "Ambiguous method call for " + methodName + " with args " + Arrays.deepToString(args) +
                            ". Multiple distinct candidates found: " + uniqueSignatures);
        }
        this.method = uniqueSignatures.get(0);
    }

    private void register(Answer<?> answer) {
        StubRule rule = new StubRule(method, args, answer);
        MockState.getInstance().getStubRegistry().registerRule(mock, rule);
    }

    private static Method resolveMethodStrict(Object mock, String methodName, Class<?>... paramTypes) {
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

    private static List<Method> collectCandidates(Object mock, String methodName) {
        List<Method> candidates = new ArrayList<>();
        Class<?> clazz = mock.getClass();

        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    candidates.add(m);
                }
            }
            clazz = clazz.getSuperclass();
        }
        // Also check public interface methods
        for(Method m : mock.getClass().getMethods()) {
             if (m.getName().equals(methodName)) {
                 candidates.add(m);
             }
        }
        return candidates.stream().distinct().collect(Collectors.toList());
    }
}