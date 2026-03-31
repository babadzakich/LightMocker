package ru.nsu;

import ru.nsu.staticmock.StaticMockSupport;

/**
 * Sample class with static methods used for testing static mocks.
 */
public class MathUtils {

    public static int add(int a, int b) throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "add", () -> a + b, a, b);
    }

    public static String greet(String name) throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "greet", () -> "Hello, " + name + "!", name);
    }

    public static int getAnswer() throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "getAnswer", () -> 42);
    }

    public static int multiply(int a, int b) throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "multiply", () -> a * b, a, b);
    }

    public static int multiply(int a, int b, int c) throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "multiply", () -> a * b * c, a, b, c);
    }

    public static int multiply(int a, int b, double c) throws Throwable {
        return StaticMockSupport.intercept(MathUtils.class, "multiply", () -> (int) (a * b * c), a, b, c);
    }

    // Instance methods for testing ordinary spies
    public int addInstance(int a, int b) {
        return a + b;
    }

    public String greetInstance(String name) {
        return "Hello, " + name + "!";
    }
}
