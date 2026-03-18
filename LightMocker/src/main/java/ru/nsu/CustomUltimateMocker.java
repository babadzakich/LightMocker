package ru.nsu;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.proxy.MockProxy;
import ru.nsu.core.proxy.MockProxyFactory;
import ru.nsu.dsl.setup.SetupBuilder;
import ru.nsu.staticmock.StaticMock;
import ru.nsu.dsl.verify.VerificationBuilder;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CustomUltimateMocker {

    public static <T> T create(Class<T> clazz) {
        return MockProxyFactory.createMock(clazz);
    }

    public static SetupBuilder<?> setup(Object mock, String methodName, Class<?>... paramTypes) {
        return new SetupBuilder<>(mock, methodName, paramTypes);
    }

    public static VerificationBuilder verify(Object mock, String methodName, Class<?>... paramTypes) {
        Method method = resolveMethod(mock, methodName, paramTypes);
        return new VerificationBuilder(mock, method);
    }

    private static Method resolveMethod(Object mock, String methodName, Class<?>... paramTypes) {
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // keep searching in superclass chain
            }
            clazz = clazz.getSuperclass();
        }
        try {
            return mock.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new MockerException("Method not found: " + methodName
                    + " with params " + Arrays.toString(paramTypes)
                    + " on " + mock.getClass().getName());
        }
    }
    public static StaticMock mockStatic(Class<?> clazz) {
        return StaticMock.mock(clazz);
    }
}
