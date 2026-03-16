package ru.nsu.core.proxy;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.state.MockState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class MockProxy implements InvocationHandler {
    private final MockState state = MockState.getInstance();

    private static final ThreadLocal<Invocation> lastInvocation = new ThreadLocal<>();


    private static final ThreadLocal<Object> lastMock = new ThreadLocal<>();

    public static Invocation getLastInvocation() {
        Invocation inv = lastInvocation.get();
        lastInvocation.remove(); // Забираем один раз
        return inv;
    }

    public static Object getLastMock() {
        return lastMock.get();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation(proxy, method, args);

        lastInvocation.set(invocation);
        lastMock.set(proxy);

        state.getInvocationRegistry().registerInvocation(proxy, invocation);

        Optional<StubRule> rule = state.getStubRegistry().findMatchingRule(proxy, invocation);

        if (rule.isPresent()) {
            return rule.get().getAnswer().answer(invocation);
        }

        return getDefaultValue(method.getReturnType());
    }


    private Object getDefaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) return false;
        if (returnType == char.class) return '\u0000';
        if (returnType == byte.class || returnType == short.class ||
                returnType == int.class || returnType == long.class) return 0;
        if (returnType == float.class) return 0.0f;
        if (returnType == double.class) return 0.0d;
        return null;
    }
}