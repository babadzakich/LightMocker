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
        return null;
    }

}