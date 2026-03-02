package ru.nsu.core.model;

import ru.nsu.core.answer.Answer;

import java.lang.reflect.Method;

public class StubRule {
    Method method;
    Object[] expectedArgs;
    Answer<?> answer;

    boolean matches(Invocation invocation) {
        if (!method.equals(invocation.method)) {
            return false;
        }
        if (expectedArgs.length != invocation.args.length) {
            return false;
        }
        for (int i = 0; i < expectedArgs.length; i++) {
            if (!expectedArgs[i].equals(invocation.args[i])) {
                return false;
            }
        }
        return true;
    }
}
