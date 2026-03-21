package ru.nsu.core.proxy;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.registry.StubRegistry;
import ru.nsu.core.state.MockState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class MockProxy implements InvocationHandler {
    private final InvocationRegistry invocationRegistry = MockState.getInstance().getInvocationRegistry();
    private final StubRegistry stubRegistry = MockState.getInstance().getStubRegistry();
    private final Object target;

    public MockProxy() {
        this.target = null;
    }

    public MockProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Handle Object methods explicitly to keep proxy identity/hash stable in registries.
        if (method.getDeclaringClass() == Object.class) {
            String name = method.getName();
            switch (name) {
                case "hashCode" -> {
                    return System.identityHashCode(proxy);
                }
                case "equals" -> {
                    Object other = (args != null && args.length > 0) ? args[0] : null;
                    return proxy == other;
                }
                case "toString" -> {
                    return (target != null ? "Spy(" : "Mock(") + proxy.getClass().getName() + ")@" + Integer.toHexString(System.identityHashCode(proxy));
                }
            }
        }

        Invocation invocation = new Invocation(proxy, method, args);
        invocationRegistry.registerInvocation(proxy, invocation);

        Optional<StubRule> rule = stubRegistry.findMatchingRule(proxy, invocation);

        if (rule.isPresent()) {
            return rule.get().getAnswer().answer(invocation);
        }

        if (target != null) {
            method.setAccessible(true);
            return method.invoke(target, args);
        }

        return getDefaultValue(method.getReturnType());
    }


    private Object getDefaultValue(Class<?> returnType) {
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