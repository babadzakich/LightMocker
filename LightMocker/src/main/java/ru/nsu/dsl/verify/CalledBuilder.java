package ru.nsu.dsl.verify;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class CalledBuilder {
    private final Object mock;
    private final Method method;
    private Object[] args = new Object[0];

    public CalledBuilder(Object mock, Method method) {
        this.mock = mock;
        this.method = method;
    }

    public CalledBuilder withArgs(Object... args) {
        this.args = args;
        return this;
    }

    public void times(int expected) {
        List<Invocation> invocations = MockState.getInstance()
                .getInvocationRegistry()
                .getInvocations(mock);

        long actual = invocations.stream()
                .filter(inv -> inv.getMethod().equals(method))
                .filter(inv -> argsMatch(inv.getArgs()))
                .count();

        if (actual != expected) {
            throw new MockerException(
                    "Expected " + method.getName() + " to be called " + expected +
                    " time(s) with args " + java.util.Arrays.toString(args) +
                    ", but was called " + actual + " time(s)"
            );
        }
    }

    private boolean argsMatch(Object[] actual) {
        if (args.length != actual.length) return false;
        for (int i = 0; i < args.length; i++) {
            if (!Objects.equals(args[i], actual[i])) return false;
        }
        return true;
    }
}
