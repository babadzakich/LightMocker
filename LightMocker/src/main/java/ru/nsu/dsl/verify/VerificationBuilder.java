package ru.nsu.dsl.verify;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VerificationBuilder {
    private final Object mock;
    private final Method method;
    private Object[] argsFilter;

    public VerificationBuilder(Object mock, Method method) {
        this.mock = mock;
        this.method = method;
    }

    public VerificationBuilder withArgs(Object... args) {
        this.argsFilter = args;
        return this;
    }

    public void times(int expectedCalls) {
        if (expectedCalls < 0) {
            throw new MockerException("times(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual != expectedCalls) {
            fail("times(" + expectedCalls + ")", actual);
        }
    }

    public void once() {
        times(1);
    }

    public void never() {
        times(0);
    }

    public void atLeast(int minCalls) {
        if (minCalls < 0) {
            throw new MockerException("atLeast(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual < minCalls) {
            fail("atLeast(" + minCalls + ")", actual);
        }
    }

    public void atMost(int maxCalls) {
        if (maxCalls < 0) {
            throw new MockerException("atMost(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual > maxCalls) {
            fail("atMost(" + maxCalls + ")", actual);
        }
    }

    public CalledBuilder called() {
        return new CalledBuilder(this);
    }

    int matchingCount() {
        InvocationRegistry registry = MockState.getInstance().getInvocationRegistry();
        List<Invocation> invocations = registry.getInvocations(mock);

        int count = 0;
        for (Invocation invocation : invocations) {
            Method invokedMethod = invocation.getMethod();
            if (!invokedMethod.getName().equals(method.getName()) ||
                    !Arrays.equals(invokedMethod.getParameterTypes(), method.getParameterTypes())) {
                continue;
            }
            if (argsFilter != null && !Arrays.deepEquals(normalize(argsFilter), normalize(invocation.getArgs()))) {
                continue;
            }
            count++;
        }
        return count;
    }

    private void fail(String expectedMode, int actualCalls) {
        throw new MockerException(
                "Verify failed for method " + method.getName() +
                        ", expected " + expectedMode +
                        ", actual " + actualCalls +
                        (argsFilter == null ? "" : ", withArgs=" + Arrays.deepToString(argsFilter))
        );
    }

    private static Object[] normalize(Object[] args) {
        if (args == null) {
            return new Object[0];
        }
        Object[] copy = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            copy[i] = Objects.requireNonNullElse(args[i], null);
        }
        return copy;
    }
}
