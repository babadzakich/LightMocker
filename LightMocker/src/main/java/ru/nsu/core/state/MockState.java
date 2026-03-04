package ru.nsu.core.state;

import ru.nsu.staticmock.StaticMock;
import java.util.Stack;

public class MockState {
    // Стек активных статических моков (для поддержки вложенных try-with-resources)
    private static final ThreadLocal<Stack<StaticMock>> staticMockStack =
            ThreadLocal.withInitial(Stack::new);

    public static void pushStatic(StaticMock mock) {
        staticMockStack.get().push(mock);
    }

    public static void popStatic() {
        staticMockStack.get().pop();
    }

    public static StaticMock getCurrentStatic() {
        Stack<StaticMock> stack = staticMockStack.get();
        return stack.isEmpty() ? null : stack.peek();
    }
}
