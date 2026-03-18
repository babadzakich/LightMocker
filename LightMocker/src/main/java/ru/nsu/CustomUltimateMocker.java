package ru.nsu;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.proxy.MockProxy;
import ru.nsu.core.proxy.MockProxyFactory;

import ru.nsu.dsl.ref.MethodRef;
import ru.nsu.dsl.ref.MethodRef1;

import ru.nsu.dsl.ref.MethodRefInt;
import ru.nsu.dsl.setup.SetupBuilder;
import ru.nsu.staticmock.StaticMock;

import java.io.Serializable;

public class CustomUltimateMocker {

    public static <T> T create(Class<T> clazz) {
        return MockProxyFactory.createMock(clazz);
    }

    public static <T, R> SetupBuilder<T, R> setup(T mock, MethodRef<T, R> ref) {
        return createSetup(mock, ref);
    }

    public static <T, R> SetupBuilder<T, R> setup(T mock, MethodRefInt<T, R> ref) {
        return createSetup(mock, ref);
    }

    public static <T, P1, R> SetupBuilder<T, R> setup2(T mock, MethodRef1<T, P1, R> ref) {
        return createSetup(mock, ref);
    }

    private static <T, R> SetupBuilder<T, R> createSetup(T mock, Serializable lambda) {
        return new SetupBuilder<>(mock, lambda);
    }

    public <R> SetupBuilder<?, R> setup(R value) {

        Invocation invocation = MockProxy.getLastInvocation();
        Object mock = MockProxy.getLastMock();

        if (invocation == null || mock == null) {
            throw new IllegalStateException("Error during setup");
        }

        return new SetupBuilder<>(mock, invocation);
    }
    // ── Static mocking ────────────────────────────────────────────────────────

    /**
     * Creates a static mock scope.
     * Use in try-with-resources; configure stubs and invoke methods via the returned object.
     * <pre>
     *   try (var mock = CustomUltimateMocker.mockStatic(MathUtils.class)) {
     *       mock.setup("add", int.class, int.class).withArgs(3, 4).returns(100);
     *       int r = mock.invoke("add", 3, 4); // 100
     *   }
     * </pre>
     */
    public static StaticMock mockStatic(Class<?> clazz) {
        return StaticMock.mock(clazz);
    }
}
