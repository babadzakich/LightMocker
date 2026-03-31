package ru.nsu.staticmock;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.model.StubRule;

import java.util.*;

/**
 * Registry for static mock stubs and invocations.
 * Keyed by the mocked class.
 */
public class StaticMockRegistry {

    private static final Map<Class<?>, List<StubRule>> stubRulesMap = new HashMap<>();
    private static final Map<Class<?>, List<Invocation>> invocationsMap = new HashMap<>();
    private static final Map<Class<?>, StaticMockMode> modesMap = new HashMap<>();

    public static void registerRule(Class<?> clazz, StubRule rule) {
        stubRulesMap.computeIfAbsent(clazz, k -> new ArrayList<>()).add(rule);
    }

    public static Optional<StubRule> findMatchingRule(Class<?> clazz, Invocation invocation) {
        List<StubRule> rules = stubRulesMap.get(clazz);
        if (rules == null || rules.isEmpty()) return Optional.empty();
        for (int i = rules.size() - 1; i >= 0; i--) {
            StubRule rule = rules.get(i);
            if (rule.matches(invocation)) return Optional.of(rule);
        }
        return Optional.empty();
    }

    public static void registerInvocation(Class<?> clazz, Invocation invocation) {
        invocationsMap.computeIfAbsent(clazz, k -> new ArrayList<>()).add(invocation);
    }

    public static void activateMock(Class<?> clazz) {
        modesMap.put(clazz, StaticMockMode.MOCK);
    }

    public static void activateSpy(Class<?> clazz) {
        modesMap.put(clazz, StaticMockMode.SPY);
    }

    public static Optional<StaticMockMode> getMode(Class<?> clazz) {
        return Optional.ofNullable(modesMap.get(clazz));
    }

    public static boolean isActive(Class<?> clazz) {
        return modesMap.containsKey(clazz);
    }

    public static List<Invocation> getInvocations(Class<?> clazz) {
        return Collections.unmodifiableList(invocationsMap.getOrDefault(clazz, Collections.emptyList()));
    }

    public static void clear(Class<?> clazz) {
        stubRulesMap.remove(clazz);
        invocationsMap.remove(clazz);
        modesMap.remove(clazz);
    }

    public static void clearAll() {
        stubRulesMap.clear();
        invocationsMap.clear();
        modesMap.clear();
    }
}
