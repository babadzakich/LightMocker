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

    private final InvocationRegistry invocationRegistry = MockState.getInstance().getInvocationRegistry();
    private final StubRegistry stubRegistry = MockState.getInstance().getStubRegistry();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Handle Object methods explicitly to keep proxy identity/hash stable in registries.
        if (method.getDeclaringClass() == Object.class) {
            String name = method.getName();
            if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            }
            if ("equals".equals(name)) {
                Object other = (args != null && args.length > 0) ? args[0] : null;
                return proxy == other;
            }
            if ("toString".equals(name)) {
                return "Mock(" + proxy.getClass().getName() + ")@" + Integer.toHexString(System.identityHashCode(proxy));
            }
        }

        Invocation invocation = new Invocation(method, args);
        invocationRegistry.registerInvocation(proxy, invocation);

        Optional<StubRule> rule = stubRegistry.findMatchingRule(proxy, invocation);

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