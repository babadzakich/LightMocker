package ru.nsu;

import ru.nsu.core.proxy.MockProxyFactory;

import ru.nsu.dsl.ref.MethodRef;
import ru.nsu.dsl.ref.MethodRef1;
import ru.nsu.dsl.ref.MethodRefExtractor;

import ru.nsu.dsl.ref.MethodRefInt;
import ru.nsu.dsl.setup.SetupBuilder;

import java.io.Serializable;
import java.lang.reflect.Method;

public class CustomUltimateMocker {

    public <T> T create(Class<T> clazz) {
        return MockProxyFactory.createMock(clazz);
    }

    public <T, R> SetupBuilder<T, R> setup(T mock, MethodRef<T, R> ref) {
        return createSetup(mock, ref);
    }

    public <T, R> SetupBuilder<T, R> setup(T mock, MethodRefInt<T, R> ref) {
        return createSetup(mock, ref);
    }

    //тут я хз как назвать, т.к. иначе ide ругается на двусмысленность
    public <T, P1, R> SetupBuilder<T, R> setup2(T mock, MethodRef1<T, P1, R> ref) {
        return createSetup(mock, ref);
    }

    private <T, R> SetupBuilder<T, R> createSetup(T mock, Serializable lambda) {
        Method method = MethodRefExtractor.extract(lambda);
        return new SetupBuilder<>(mock, method);
    }

}
