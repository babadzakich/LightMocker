package ru.nsu.dsl.ref;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodRefExtractor {
    public static Method extract(Serializable lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);

            String className = serializedLambda.getImplClass().replace('/', '.');
            String methodName = serializedLambda.getImplMethodName();
            String signature = serializedLambda.getImplMethodSignature();

            String paramsPart = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')'));
            Class<?>[] paramTypes = parseParamTypes(paramsPart);

            Class<?> clazz = Class.forName(className);
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName) &&
                        java.util.Arrays.equals(m.getParameterTypes(), paramTypes)) {
                    return m;
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to extract method from lambda: " + e);
        }
        return null;
    }

    /**
     * Перегрузка с явными типами — для перегруженных методов:
     * MethodRefExtractor.extract(UserService::find, long.class)
     */
    public static Method extract(Serializable lambda, Class<?>... paramTypes) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);

            String className = serializedLambda.getImplClass().replace('/', '.');
            String methodName = serializedLambda.getImplMethodName();

            Class<?> clazz = Class.forName(className);
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName) &&
                        java.util.Arrays.equals(m.getParameterTypes(), paramTypes)) {
                    return m;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to extract method from lambda: " + e);
        }
        return null;
    }

    /**
     * Парсит JVM-дескриптор параметров, например "Ljava/lang/String;IZ" → [String.class, int.class, boolean.class]
     */
    private static Class<?>[] parseParamTypes(String params) throws ClassNotFoundException {
        List<Class<?>> types = new ArrayList<>();
        int i = 0;
        while (i < params.length()) {
            char c = params.charAt(i);
            switch (c) {
                case 'Z' -> { types.add(boolean.class); i++; }
                case 'B' -> { types.add(byte.class);    i++; }
                case 'C' -> { types.add(char.class);    i++; }
                case 'S' -> { types.add(short.class);   i++; }
                case 'I' -> { types.add(int.class);     i++; }
                case 'J' -> { types.add(long.class);    i++; }
                case 'F' -> { types.add(float.class);   i++; }
                case 'D' -> { types.add(double.class);  i++; }
                case 'L' -> {
                    // "Ljava/lang/String;" → java.lang.String
                    int end = params.indexOf(';', i);
                    String typeName = params.substring(i + 1, end).replace('/', '.');
                    types.add(Class.forName(typeName));
                    i = end + 1;
                }
                case '[' -> {
                    // массив — рекурсивно через Class.forName с дескриптором
                    int end = i;
                    while (params.charAt(end) == '[') end++;
                    if (params.charAt(end) == 'L') {
                        end = params.indexOf(';', end);
                    }
                    String arrayDesc = params.substring(i, end + 1);
                    types.add(Class.forName(arrayDesc.replace('/', '.')));
                    i = end + 1;
                }
                default -> i++;
            }
        }
        return types.toArray(new Class<?>[0]);
    }
}

