package ru.nsu.core.registry;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;

import java.util.*;

/**
 * Registry for storing and finding stubbing rules for mock objects.
 */
public class StubRegistry {
    // IdentityHashMap ensures that mocks are distinguished by reference equality
    private final Map<Object, List<StubRule>> stubRulesMap = new IdentityHashMap<>();

    /**
     * Registers a new stubbing rule for a mock.
     */
    public void registerRule(Object mock, StubRule rule) {
        stubRulesMap.computeIfAbsent(mock, k -> new ArrayList<>()).add(rule);
    }

    /**
     * Finds the most recently added rule that matches the given invocation.
     */
    public Optional<StubRule> findMatchingRule(Object mock, Invocation invocation) {
        List<StubRule> rules = stubRulesMap.get(mock);
        if (rules == null || rules.isEmpty()) {
            return Optional.empty();
        }

        // Iterate in reverse order so the last registered rule wins
        for (int i = rules.size() - 1; i >= 0; i--) {
            StubRule rule = rules.get(i);
            if (rule.matches(invocation)) {
                return Optional.of(rule);
            }
        }
        return Optional.empty();
    }

    /**
     * Clears all configured stub rules.
     */
    public void clear() {
        stubRulesMap.clear();
    }
}
