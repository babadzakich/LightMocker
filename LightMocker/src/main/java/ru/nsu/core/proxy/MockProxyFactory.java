package ru.nsu.core.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Proxy;

public class MockProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T createMock(Class<T> clazz) {
        MockProxy handler = new MockProxy();

        if (clazz.isInterface()) {
            return (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class<?>[]{clazz},
                    handler
            );
        } else {
            try {
                return new ByteBuddy()
                        .subclass(clazz)
                        .method(ElementMatchers.any())
                        .intercept(InvocationHandlerAdapter.of(handler))
                        .make()
                        .load(clazz.getClassLoader())
                        .getLoaded()
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (Exception e) {
                System.err.println("Failed to mock class: " + clazz.getName() + " " + e.getMessage());
            }
        }
    }
}
