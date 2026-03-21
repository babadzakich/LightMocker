package ru.nsu.core.registry;

import ru.nsu.core.model.Invocation;

import java.util.*;

/**
 * Registry for recording and retrieving method invocations on mock objects.
 */
public class InvocationRegistry {
    // IdentityHashMap ensures that mocks are distinguished by reference equality
    private final Map<Object, List<Invocation>> invocationsMap = new IdentityHashMap<>();
    private final List<Invocation> globalInvocations = new ArrayList<>();

    /**
     * Records a single invocation for a given mock.
     */
    public void registerInvocation(Object mock, Invocation invocation) {
        invocationsMap.computeIfAbsent(mock, k -> new ArrayList<>()).add(invocation);
        globalInvocations.add(invocation);
    }

    /**
     * Returns a list of all invocations made to the specified mock.
     */
    public List<Invocation> getInvocations(Object mock) {
        return Collections.unmodifiableList(invocationsMap.getOrDefault(mock, Collections.emptyList()));
    }

    /**
     * Returns a list of all recorded invocations in chronological order.
     */
    public List<Invocation> getGlobalInvocations() {
        return Collections.unmodifiableList(new ArrayList<>(globalInvocations));
    }

    /**
     * Clears all recorded invocations.
     */
    public void clear() {
        invocationsMap.clear();
        globalInvocations.clear();
    }
}
