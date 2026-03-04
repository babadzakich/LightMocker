package ru.nsu.core.proxy;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;
import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.registry.StubRegistry;
import ru.nsu.core.state.MockState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation();
        invocation.setMethod(method);
        invocation.setArgs((args == null) ? new Object[0] : args);
        invocation.setTimestamp(System.currentTimeMillis());


        InvocationRegistry.record(proxy, invocation);

        StubRule rule = StubRegistry.findRule(proxy, invocation);

        if (rule != null) {
            return rule.getAnswer().answer(invocation);
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