package ru.nsu;

import ru.nsu.core.proxy.MockProxyFactory;
import ru.nsu.dsl.ref.MethodRef;
import ru.nsu.dsl.setup.SetupBuilder;
import ru.nsu.dsl.verify.VerificationBuilder;

public class CustomUltimateMocker {

    public static <T> T create(Class<T> clazz) {
        return MockProxyFactory.createMock(clazz);
    }

    public static <T> SetupBuilder setup(T mock, MethodRef<T, ?> lambda) {
        return new SetupBuilder(mock, lambda);
    }

    public static SetupBuilder setup(Object mock, String methodName, Class<?>... paramTypes) {
        return new SetupBuilder(mock, methodName, paramTypes);
    }

    public static VerificationBuilder verify(Object mock) {
        return new VerificationBuilder(mock);
    }
}
