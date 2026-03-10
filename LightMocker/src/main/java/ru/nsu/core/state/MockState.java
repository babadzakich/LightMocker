package ru.nsu.core.state;

import lombok.Getter;
import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.registry.StubRegistry;

/**
 * Global state for the mocking framework.
 * Provides access to the registries for invocations and stubbing rules.
 */
@Getter
public class MockState {
    @Getter
    private static final MockState Instance = new MockState();

    private final InvocationRegistry invocationRegistry = new InvocationRegistry();
    private final StubRegistry stubRegistry = new StubRegistry();

    private MockState() {
    }

    /**
     * Resets both registries. Useful for cleaning up between tests.
     */
    public void reset() {
        invocationRegistry.clear();
        stubRegistry.clear();
    }
}
