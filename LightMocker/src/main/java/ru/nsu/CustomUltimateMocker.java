package ru.nsu;

import ru.nsu.core.proxy.MockProxyFactory;
import ru.nsu.dsl.setup.SetupBuilder;
import ru.nsu.staticmock.StaticMock;
import ru.nsu.staticmock.StaticSpy;
import ru.nsu.dsl.verify.VerificationBuilder;

public class CustomUltimateMocker {

    public static <T> T create(Class<T> clazz) {
        return MockProxyFactory.createMock(clazz);
    }

    public static SetupBuilder<?> setup(Object mock, String methodName, Class<?>... paramTypes) {
        return new SetupBuilder<>(mock, methodName, paramTypes);
    }

    public static VerificationBuilder verify(Object mock, String methodName, Class<?>... paramTypes) {
        return new VerificationBuilder(mock, methodName, paramTypes);
    }

    public static StaticMock mockStatic(Class<?> clazz) {
        return StaticMock.mock(clazz);
    }

    public static StaticSpy spyStatic(Class<?> clazz) {
        return StaticSpy.spy(clazz);
    }
}
