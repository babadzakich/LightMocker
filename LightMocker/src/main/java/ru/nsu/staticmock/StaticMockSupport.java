package ru.nsu.staticmock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.asm.Advice;
import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.util.MethodUtils;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.isSynthetic;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.isTypeInitializer;

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

    public static void redefineForStaticMock(Class<?> targetClass) {
        try {
            new ByteBuddy()
                    .redefine(targetClass, ClassFileLocator.ForClassLoader.of(targetClass.getClassLoader()))
                    .visit(Advice.to(StaticMethodAdvice.class)
                            .on(isStatic()
                                    .and(not(isTypeInitializer()))
                                    .and(not(isSynthetic()))
                                    .and(not(isAbstract()))))
                    .make()
                    .load(
                            targetClass.getClassLoader(),
                            ClassReloadingStrategy.of(StaticMockAgent.getInstrumentation()));
        } catch (Exception e) {
            throw new MockerException("Failed to redefine static mock class for " + targetClass.getName(), e);
        }
    }

    public static void restoreOriginalClass(Class<?> targetClass) {
        try {
            new ByteBuddy()
                    .redefine(targetClass, ClassFileLocator.ForClassLoader.of(targetClass.getClassLoader()))
                    .make()
                    .load(
                            targetClass.getClassLoader(),
                            ClassReloadingStrategy.of(StaticMockAgent.getInstrumentation()));
        } catch (Exception e) {
            throw new MockerException("Failed to restore original class for " + targetClass.getName(), e);
        }
    }

    public static InterceptDecision handleStaticCall(
            Class<?> targetClass,
            Method method,
            Object[] args
    ) throws Throwable {
        Object[] safeArgs = args != null ? args : new Object[0];
        Invocation invocation = new Invocation(targetClass, method, safeArgs);
        StaticMockRegistry.registerInvocation(targetClass, invocation);

        Optional<StubRule> rule = StaticMockRegistry.findMatchingRule(targetClass, invocation);
        if (rule.isPresent()) {
            return InterceptDecision.skipWithReturn(rule.get().getAnswer().answer(invocation));
        }

        StaticMockMode mode = StaticMockRegistry.getMode(targetClass)
                .orElseThrow(() -> new MockerException("Static mock mode is not configured for " + targetClass.getName()));

        if (mode == StaticMockMode.MOCK) {
            return InterceptDecision.skipWithReturn(getDefaultValue(method.getReturnType()));
        }

        return InterceptDecision.proceed();
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

    public static final class InterceptDecision {
        public final boolean skipOriginal;
        public final Object returnValue;
        public final Throwable throwable;

        private InterceptDecision(boolean skipOriginal, Object returnValue, Throwable throwable) {
            this.skipOriginal = skipOriginal;
            this.returnValue = returnValue;
            this.throwable = throwable;
        }

        public static InterceptDecision proceed() {
            return new InterceptDecision(false, null, null);
        }

        public static InterceptDecision skipWithReturn(Object value) {
            return new InterceptDecision(true, value, null);
        }

        public static InterceptDecision skipWithThrowable(Throwable throwable) {
            return new InterceptDecision(true, null, throwable);
        }
    }

    public static class StaticMethodAdvice {

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean onEnter(
                @Advice.Origin Class<?> targetClass,
                @Advice.Origin Method method,
                @Advice.AllArguments Object[] args,
                @Advice.Local("decision") InterceptDecision decision
        ) throws Throwable {
            if (!StaticMockRegistry.isActive(targetClass)) {
                decision = InterceptDecision.proceed();
                return false;
            }

            decision = handleStaticCall(targetClass, method, args);
            return decision.skipOriginal;
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void onExit(
                @Advice.Enter boolean skipped,
                @Advice.Local("decision") InterceptDecision decision,
                @Advice.Return(readOnly = false, typing = net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC) Object returned,
                @Advice.Thrown(readOnly = false) Throwable thrown
        ) throws Throwable {
            if (!skipped || decision == null) {
                return;
            }

            if (decision.throwable != null) {
                thrown = decision.throwable;
                return;
            }

            returned = decision.returnValue;
            thrown = null;
        }
    }
}
