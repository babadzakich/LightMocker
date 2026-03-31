package ru.nsu.staticmock;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.util.MethodUtils;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class StaticMockSupport {

    private StaticMockSupport() {
    }

    @FunctionalInterface
    public interface ThrowingSupplier<R> {
        R get() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    @SuppressWarnings("unchecked")
    public static <R> R intercept(
            Class<?> targetClass,
            String methodName,
            ThrowingSupplier<R> realCall,
            Object... args
    ) throws Throwable {
        if (!StaticMockRegistry.isActive(targetClass)) {
            return realCall.get();
        }

        Object[] safeArgs = args != null ? args : new Object[0];
        Method method = resolveMethod(targetClass, methodName, safeArgs);
        Invocation invocation = new Invocation(targetClass, method, safeArgs);
        StaticMockRegistry.registerInvocation(targetClass, invocation);

        Optional<StubRule> rule = StaticMockRegistry.findMatchingRule(targetClass, invocation);
        if (rule.isPresent()) {
            return (R) rule.get().getAnswer().answer(invocation);
        }

        StaticMockMode mode = StaticMockRegistry.getMode(targetClass)
                .orElseThrow(() -> new MockerException("Static mock mode is not configured for " + targetClass.getName()));

        if (mode == StaticMockMode.MOCK) {
            return (R) getDefaultValue(method.getReturnType());
        }

        return realCall.get();
    }

    public static void interceptVoid(
            Class<?> targetClass,
            String methodName,
            ThrowingRunnable realCall,
            Object... args
    ) throws Throwable {
        intercept(targetClass, methodName, () -> {
            realCall.run();
            return null;
        }, args);
    }

    static Method resolveMethod(Class<?> targetClass, String methodName, Object[] args) {
        List<Method> candidates = new ArrayList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && MethodUtils.isCompatible(method, args)) {
                candidates.add(method);
            }
        }

        if (candidates.isEmpty()) {
            throw new MockerException(
                    "Static method not found: " + methodName
                            + " compatible with args " + Arrays.toString(args) + " on " + targetClass.getName());
        }

        if (candidates.size() > 1) {
            throw new MockerException(
                    "Ambiguous static method call: " + methodName
                            + " with args " + Arrays.toString(args) + " matches multiple candidates: " + candidates);
        }

        return candidates.get(0);
    }

    static Object getDefaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) return false;
        if (returnType == char.class) return '\u0000';
        if (returnType == byte.class) return (byte) 0;
        if (returnType == short.class) return (short) 0;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        if (returnType == float.class) return 0.0f;
        if (returnType == double.class) return 0.0d;
        return null;
    }
}
