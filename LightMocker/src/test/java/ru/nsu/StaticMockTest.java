package ru.nsu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.nsu.CustomUltimateMocker.mockStatic;

public class StaticMockTest {

    @Test
    void testStaticMockReturnsCustomValue() throws Throwable {
        assertEquals(7, MathUtils.add(3, 4));

        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("add")
                    .withArgs(3, 4)
                    .returns(100);

            int result = mock.invoke("add", 3, 4);
            System.out.println("Mocked add(3,4) = " + result);
            assertEquals(100, result);

            int other = mock.invoke("add", 1, 2);
            System.out.println("Unstubbed add(1,2) = " + other);
            assertEquals(0, other);
        }

        assertEquals(7, MathUtils.add(3, 4));
    }

    @Test
    void testStaticMockWithStringReturn() throws Throwable {
        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("greet", String.class)
                    .withArgs("World")
                    .returns("Mocked!");

            String result = mock.invoke("greet", "World");
            System.out.println("Mocked greet = " + result);
            assertEquals("Mocked!", result);
        }

        // Restored
        assertEquals("Hello, World!", MathUtils.greet("World"));
    }

    @Test
    void testStaticMockNoArgs() throws Throwable {
        //privet medved
        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("getAnswer")
                    .returns(999);

            int result = mock.invoke("getAnswer");
            System.out.println("Mocked getAnswer() = " + result);
            assertEquals(999, result);
        }

        assertEquals(42, MathUtils.getAnswer());
    }

    @Test
    void testStaticMockThrows() {
        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("add")
                    .withArgs(0, 0)
                    .thenThrow(new ArithmeticException("mocked error"));

            assertThrows(RuntimeException.class, () -> mock.invoke("add", 0, 0));
        }
    }

    @Test
    void testStaticSpy() throws Throwable {
        assertEquals(7, MathUtils.add(3, 4));

        try (var spy = CustomUltimateMocker.spyStatic(MathUtils.class)) {
            // Stub one specific call
            spy.setup("add")
                    .withArgs(10, 20)
                    .returns(100);

            int result = spy.invoke("add", 10, 20);
            assertEquals(100, result);

            int realResult = spy.invoke("add", 3, 4);
            assertEquals(7, realResult);
        }
    }

    @Test
    void testSmartSetupInference() throws Throwable {
        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("add")
                .withArgs(50, 50)
                .returns(100);

            int result = mock.invoke("add", 50, 50);
            assertEquals(100, result);
        }
    }

    @Test
    void testOverloadedMethodMocking() throws Throwable {
        try (var mock = mockStatic(MathUtils.class)) {
            mock.setup("multiply")
                .withArgs(1, 2)
                .returns(10);
            mock.setup("multiply")
                    .withArgs(1, 2, 3)
                    .returns(100);
            mock.setup("multiply")
                    .withArgs(1, 2, 3.3)
                    .returns(200);
            int result = mock.invoke("multiply", 1, 2);
            int result2 = mock.invoke("multiply", 1, 2, 3);
            int result3 = mock.invoke("multiply", 1, 2, 3.3);
            assertEquals(10, result);
            assertEquals(100, result2);
            assertEquals(200, result3);
        }
    }
}
