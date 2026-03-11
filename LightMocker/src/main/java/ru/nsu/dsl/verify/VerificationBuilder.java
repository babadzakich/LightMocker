package ru.nsu.dsl.verify;

import ru.nsu.dsl.ref.MethodRef;
import ru.nsu.dsl.ref.MethodRefExtractor;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;

public class VerificationBuilder {
    private final Object mock;

    public VerificationBuilder(Object mock) {
        this.mock = mock;
    }

    public <T> CalledBuilder called(MethodRef<T, ?> lambda) {
        return new CalledBuilder(mock, MethodRefExtractor.extract(lambda));
    }

    public CalledBuilder called(String methodName, Class<?>... paramTypes) {
        return new CalledBuilder(mock, resolveMethod(mock, methodName, paramTypes));
    }

    private static Method resolveMethod(Object mock, String methodName, Class<?>... paramTypes) {
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // continue
            }
            clazz = clazz.getSuperclass();
        }
        try {
            return mock.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new MockerException("Method not found: " + methodName
                    + " with params " + java.util.Arrays.toString(paramTypes)
                    + " on " + mock.getClass().getName());
        }
    }
}
