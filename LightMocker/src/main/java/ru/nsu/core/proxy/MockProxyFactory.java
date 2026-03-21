package ru.nsu.core.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Proxy;

public class MockProxyFactory {

    public static <T> T createMock(Class<T> clazz) {
        return createProxy(clazz, new MockProxy());
    }

    public static <T> T createSpy(T target) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) target.getClass();
        return createProxy(clazz, new MockProxy(target));
    }

    @SuppressWarnings("unchecked")
    private static <T> T createProxy(Class<T> clazz, MockProxy handler) {
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
                throw new ru.nsu.exception.MockerException("Failed to create proxy for " + clazz.getName() + " " + e.getMessage());
            }
        }
    }
}
