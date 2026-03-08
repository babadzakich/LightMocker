package ru.nsu.core.state;

import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.registry.StubRegistry;

/**
 * Global state for the mocking framework.
 * Provides access to the registries for invocations and stubbing rules.
 */
public class MockState {
    private static final MockState INSTANCE = new MockState();

    private final InvocationRegistry invocationRegistry = new InvocationRegistry();
    private final StubRegistry stubRegistry = new StubRegistry();

    private MockState() {
    }

    public static MockState getInstance() {
        return INSTANCE;
    }

    public InvocationRegistry getInvocationRegistry() {
        return invocationRegistry;
    }

    public StubRegistry getStubRegistry() {
        return stubRegistry;
    }

    /**
     * Resets both registries. Useful for cleaning up between tests.
     */
    public void reset() {
        invocationRegistry.clear();
        stubRegistry.clear();
    }
}
