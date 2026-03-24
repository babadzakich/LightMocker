package ru.nsu.core.util;

import java.lang.reflect.Method;

public class MethodUtils {

    public static boolean isCompatible(Method m, Object[] args) {
        if (m.getParameterCount() != args.length) {
            return false;
        }
        Class<?>[] paramTypes = m.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                // null matches any object type, but not primitive
                if (paramTypes[i].isPrimitive()) {
                    return false;
                }
                continue;
            }
            Class<?> argType = arg.getClass();
            Class<?> paramType = paramTypes[i];

            // Check for primitive wrappers mismatch (e.g. Integer passed to int)
            if (paramType.isPrimitive()) {
                if (paramType == int.class && argType == Integer.class) continue;
                if (paramType == long.class && argType == Long.class) continue;
                if (paramType == double.class && argType == Double.class) continue;
                if (paramType == float.class && argType == Float.class) continue;
                if (paramType == boolean.class && argType == Boolean.class) continue;
                if (paramType == char.class && argType == Character.class) continue;
                if (paramType == byte.class && argType == Byte.class) continue;
                if (paramType == short.class && argType == Short.class) continue;
                return false;
            }

            if (!paramType.isAssignableFrom(argType)) {
                return false;
            }
        }
        return true;
    }
}

