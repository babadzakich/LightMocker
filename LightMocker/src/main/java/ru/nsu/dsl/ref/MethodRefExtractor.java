package ru.nsu.dsl.ref;

import net.bytebuddy.asm.Advice;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class MethodRefExtractor {
    public static Method extract(Serializable lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);

            String className = serializedLambda.getImplClass().replace('/', '.');
            String methodName = serializedLambda.getImplMethodName();

            Class<?> clazz = Class.forName(className);
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) return m;
            }

        } catch (Exception e) {
            System.err.println("Failed to extract method from lambda " +  e);
            return null;

        }
        return null;
    }
}