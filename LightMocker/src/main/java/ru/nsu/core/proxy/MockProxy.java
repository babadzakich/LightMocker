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

    InvocationRegistry invocationRegistry = new InvocationRegistry();
    StubRegistry stubRegistry = new StubRegistry();
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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