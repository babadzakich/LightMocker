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

    /**
     * Checks if this rule matches the given invocation.
     */
    public boolean matches(Invocation invocation) {
        Method invokedMethod = invocation.getMethod();
        if (!method.getName().equals(invokedMethod.getName())) {
            return false;
        }
        if (!java.util.Arrays.equals(method.getParameterTypes(), invokedMethod.getParameterTypes())) {
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

    public Answer<?> getAnswer() {
        return answer;
    }

    public Method getMethod() {
        return method;
    }
}
