package ru.nsu;

/**
 * Sample class with static methods used for testing static mocks.
 */
public class MathUtils {

    public static int add(int a, int b) {
        return a + b;
    }

    public static String greet(String name) {
        return "Hello, " + name + "!";
    }

    public static int getAnswer() {
        return 42;
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int multiply(int a, int b, int c) {
        return a * b * c;
    }

    public static int multiply(int a, int b, double c) {
        return (int) (a * b * c);
    }
}

