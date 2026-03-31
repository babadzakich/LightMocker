package ru.nsu.staticmock;

import java.lang.reflect.Method;

/**
 * Scope-based static spy. No Java agent, no bytecode rewriting.
 * All calls go through {@link #invoke(String, Object...)}.
 * <p>
 * If a method is stubbed, the stub is executed.
 * If not, the real static method is called.
 */
public class StaticSpy implements AutoCloseable {

    private final Class<?> targetClass;

    private StaticSpy(Class<?> targetClass) {
        this.targetClass = targetClass;
        StaticMockRegistry.activateSpy(targetClass);
        StaticMockSupport.redefineForStaticMock(targetClass);
    }

    public static StaticSpy spy(Class<?> clazz) {
        return new StaticSpy(clazz);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Creates a setup builder for configuring the static method stub.
     */
    public <R> StaticSetupBuilder<R> setup(String methodName, Class<?>... paramTypes) {
        if (paramTypes != null && paramTypes.length > 0) {
            try {
                Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
                return new StaticSetupBuilder<>(targetClass, method);
            } catch (NoSuchMethodException e) {
                throw new ru.nsu.exception.MockerException(
                        "Static method not found: " + methodName + " on " + targetClass.getName());
            }
        }

        java.util.List<Method> candidates = java.util.Arrays.stream(targetClass.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .collect(java.util.stream.Collectors.toList());

        if (candidates.isEmpty()) {
            throw new ru.nsu.exception.MockerException(
                    "Static method not found: " + methodName + " on " + targetClass.getName());
        }

        return new StaticSetupBuilder<>(targetClass, methodName, candidates);
    }

    /**
     * Invokes a static method through the spy.
     * If a matching stub exists — returns the stubbed value.
     * Otherwise — calls the real method.
     */
    @SuppressWarnings("unchecked")
    public <R> R invoke(String methodName, Object... args) throws Throwable {
        Object[] safeArgs = args != null ? args : new Object[0];
        Method method = resolveMethod(methodName, safeArgs);
        method.setAccessible(true);
        return (R) method.invoke(null, safeArgs);
    }

    @Override
    public void close() {
        StaticMockRegistry.clear(targetClass);
        StaticMockSupport.restoreOriginalClass(targetClass);
    }

    private Method resolveMethod(String methodName, Object[] args) {
        return StaticMockSupport.resolveMethod(targetClass, methodName, args);
    }
}
