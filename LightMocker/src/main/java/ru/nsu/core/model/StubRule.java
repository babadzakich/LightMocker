package ru.nsu.core.model;

import ru.nsu.core.answer.Answer;

import java.lang.reflect.Method;

public class StubRule {
    private Method method;
    private Object[] expectedArgs;
    private Answer<?> answer;

    public Answer<?> getAnswer() {
        return answer;
    }

    boolean matches(Invocation invocation) {
        if (!method.equals(invocation.getMethod())) {
            return false;
        }
        if (expectedArgs.length != invocation.getArgs().length) {
            return false;
        }
        for (int i = 0; i < expectedArgs.length; i++) {
            if (!expectedArgs[i].equals(invocation.getArgs()[i])) {
                return false;
            }
        }
        return true;
    }
}
