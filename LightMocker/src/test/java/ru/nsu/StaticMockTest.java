package ru.nsu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StaticMockTest {

    @Test
    void testStaticMockReturnsCustomValue() throws Throwable {
        assertEquals(7, MathUtils.add(3, 4));

        try (var mock = CustomUltimateMocker.mockStatic(MathUtils.class)) {
            mock.setup("add", int.class, int.class)
                    .withArgs(3, 4)
                    .returns(100);

            int result = mock.invoke("add", 3, 4);
            System.out.println("Mocked add(3,4) = " + result);
            assertEquals(100, result);

            int other = mock.invoke("add", 1, 2);
            System.out.println("Unstubbed add(1,2) = " + other);
            assertEquals(3, other);
        }

        assertEquals(7, MathUtils.add(3, 4));
    }

    @Test
    void testStaticMockWithStringReturn() throws Throwable {
        try (var mock = CustomUltimateMocker.mockStatic(MathUtils.class)) {
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
        try (var mock = CustomUltimateMocker.mockStatic(MathUtils.class)) {
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
        try (var mock = CustomUltimateMocker.mockStatic(MathUtils.class)) {
            mock.setup("add", int.class, int.class)
                    .withArgs(0, 0)
                    .thenThrow(new ArithmeticException("mocked error"));

            assertThrows(RuntimeException.class, () -> mock.invoke("add", 0, 0));
        }
    }
}
