package ru.nsu.dsl.verify;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.registry.InvocationRegistry;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.nsu.core.util.MethodUtils;

public class VerificationBuilder {
    private final Object mock;
    private final String methodName;
    private Method method;
    private final List<Method> candidates;
    private Object[] argsFilter;

    public VerificationBuilder(Object mock, String methodName, Class<?>... paramTypes) {
        this.mock = mock;
        this.methodName = methodName;

        if (paramTypes != null && paramTypes.length > 0) {
            this.method = resolveMethodStrict(mock, methodName, paramTypes);
            this.candidates = null;
        } else {
            this.candidates = collectCandidates(mock, methodName);
            if (candidates.isEmpty()) {
                throw new MockerException("Method not found: " + methodName + " on " + mock.getClass().getName());
            }
            this.method = null;
        }
    }

    public VerificationBuilder withArgs(Object... args) {
        this.argsFilter = args;
        if (method == null) {
            resolveMethodByArgs();
        }
        return this;
    }

    public void times(int expectedCalls) {
        ensureMethodResolved();
        if (expectedCalls < 0) {
            throw new MockerException("times(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual != expectedCalls) {
            fail("times(" + expectedCalls + ")", actual);
        }
    }

    public void once() {
        times(1);
    }

    public void never() {
        times(0);
    }

    public void atLeast(int minCalls) {
        ensureMethodResolved();
        if (minCalls < 0) {
            throw new MockerException("atLeast(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual < minCalls) {
            fail("atLeast(" + minCalls + ")", actual);
        }
    }

    public void atMost(int maxCalls) {
        ensureMethodResolved();
        if (maxCalls < 0) {
            throw new MockerException("atMost(n): n must be >= 0");
        }
        int actual = matchingCount();
        if (actual > maxCalls) {
            fail("atMost(" + maxCalls + ")", actual);
        }
    }

    public CalledBuilder called() {
        return new CalledBuilder(this);
    }

    private void ensureMethodResolved() {
        if (method != null) return;

        if (argsFilter != null) {
            resolveMethodByArgs();
            return;
        }

        // De-duplicate candidates by signature
        List<Method> uniqueSignatures = deduplicate(candidates);

        if (uniqueSignatures.size() == 1) {
            this.method = uniqueSignatures.get(0);
        } else {
             throw new MockerException(
                    "Ambiguous verification for method '" + methodName +
                    "'. Multiple overloads found and no arguments specified to disambiguate. " +
                    "Candidates: " + uniqueSignatures);
        }
    }

    private void resolveMethodByArgs() {
        List<Method> compatible = candidates.stream()
                .filter(m -> MethodUtils.isCompatible(m, argsFilter))
                .toList();

        if (compatible.isEmpty()) {
             throw new MockerException(
                    "No method found for " + methodName + " compatible with args " + Arrays.deepToString(argsFilter) +
                    " on " + mock.getClass().getName());
        }

        List<Method> uniqueSignatures = deduplicate(compatible);

        if (uniqueSignatures.size() > 1) {
             throw new MockerException(
                    "Ambiguous verification for " + methodName + " with args " + Arrays.deepToString(argsFilter) +
                            ". Multiple distinct candidates found: " + uniqueSignatures);
        }
        this.method = uniqueSignatures.get(0);
    }

    private List<Method> deduplicate(List<Method> methods) {
        List<Method> uniqueSignatures = new ArrayList<>();
        for (Method m : methods) {
            boolean duplicate = false;
            for (Method existing : uniqueSignatures) {
                if (Arrays.equals(m.getParameterTypes(), existing.getParameterTypes())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                uniqueSignatures.add(m);
            }
        }
        return uniqueSignatures;
    }

    int matchingCount() {
        InvocationRegistry registry = MockState.getInstance().getInvocationRegistry();
        List<Invocation> invocations = registry.getInvocations(mock);

        int count = 0;
        for (Invocation invocation : invocations) {
            Method invokedMethod = invocation.getMethod();
            if (!invokedMethod.getName().equals(method.getName()) ||
                    !Arrays.equals(invokedMethod.getParameterTypes(), method.getParameterTypes())) {
                continue;
            }
            if (argsFilter != null && !Arrays.deepEquals(normalize(argsFilter), normalize(invocation.getArgs()))) {
                continue;
            }
            count++;
        }
        return count;
    }

    private void fail(String expectedMode, int actualCalls) {
        throw new MockerException(
                "Verify failed for method " + method.getName() +
                        ", expected " + expectedMode +
                        ", actual " + actualCalls +
                        (argsFilter == null ? "" : ", withArgs=" + Arrays.deepToString(argsFilter))
        );
    }

    private static Object[] normalize(Object[] args) {
        if (args == null) {
            return new Object[0];
        }
        Object[] copy = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            copy[i] = Objects.requireNonNullElse(args[i], null);
        }
        return copy;
    }

    private static Method resolveMethodStrict(Object mock, String methodName, Class<?>... paramTypes) {
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // keep searching in superclass chain
            }
            clazz = clazz.getSuperclass();
        }
        try {
            return mock.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new MockerException("Method not found: " + methodName
                    + " with params " + java.util.Arrays.toString(paramTypes)
                    + " on " + mock.getClass().getName());
        }
    }

    private static List<Method> collectCandidates(Object mock, String methodName) {
        List<Method> candidates = new ArrayList<>();
        Class<?> clazz = mock.getClass();

        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    candidates.add(m);
                }
            }
            clazz = clazz.getSuperclass();
        }
        for(Method m : mock.getClass().getMethods()) {
             if (m.getName().equals(methodName)) {
                 candidates.add(m);
             }
        }
        return candidates.stream().distinct().collect(Collectors.toList());
    }
}
