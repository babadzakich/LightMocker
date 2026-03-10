package ru.nsu.core.model;

import ru.nsu.core.answer.Answer;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Represents a rule for mock behavior.
 */
public class StubRule {
    private final Method method;
    private final Object[] expectedArgs;
    private final Answer<?> answer;

    public StubRule(Method method, Object[] expectedArgs, Answer<?> answer) {
        this.method = method;
        this.expectedArgs = expectedArgs != null ? expectedArgs : new Object[0];
        this.answer = answer;
    }

    public Method getMethod()    { return method; }
    public Answer<?> getAnswer() { return answer; }

    /**
     * Checks if this rule matches the given invocation.
     */
    public boolean matches(Invocation invocation) {
        if (!methodsMatch(method, invocation.getMethod())) {
            return false;
        }

        Object[] actualArgs = invocation.getArgs();
        if (expectedArgs.length != actualArgs.length) {
            return false;
        }

        for (int i = 0; i < expectedArgs.length; i++) {
            if (!Objects.equals(expectedArgs[i], actualArgs[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Сравнивает методы по имени и типам параметров (игнорируя declaringClass).
     * Нужно потому что ByteBuddy subclass может иметь другой declaringClass.
     */
    private boolean methodsMatch(Method a, Method b) {
        return a.getName().equals(b.getName())
                && java.util.Arrays.equals(a.getParameterTypes(), b.getParameterTypes());
    }
}
