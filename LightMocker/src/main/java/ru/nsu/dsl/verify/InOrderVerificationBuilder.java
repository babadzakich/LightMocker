package ru.nsu.dsl.verify;

import ru.nsu.core.util.MethodUtils;
import ru.nsu.exception.MockerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InOrderVerificationBuilder {
    private final InOrder inOrder;
    private final Object mock;
    private final String methodName;
    private Method method;
    private final List<Method> candidates;
    private Object[] argsFilter;

    public InOrderVerificationBuilder(InOrder inOrder, Object mock, String methodName, Class<?>... paramTypes) {
        this.inOrder = inOrder;
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

    public InOrderVerificationBuilder withArgs(Object... args) {
        this.argsFilter = args;
        if (method == null) {
            resolveMethodByArgs();
        }
        return this;
    }

    public void once() {
        ensureMethodResolved();
        inOrder.verifyCall(mock, method, argsFilter);
    }

    // Optional: support other modes if needed. For now, once() is most common in InOrder.
    public void times(int n) {
        if (n < 0) throw new MockerException("times(n): n must be >= 0");
        for (int i = 0; i < n; i++) {
            once();
        }
    }

    private void ensureMethodResolved() {
        if (method != null) return;
        
        if (argsFilter != null) {
            resolveMethodByArgs();
            return;
        }

        List<Method> unique = deduplicate(candidates);
        if (unique.size() == 1) {
            this.method = unique.get(0);
        } else {
            throw new MockerException("Ambiguous InOrder verification for method '" + methodName + "'. Candidates: " + unique);
        }
    }

    private void resolveMethodByArgs() {
        List<Method> compatible = candidates.stream()
                .filter(m -> MethodUtils.isCompatible(m, argsFilter))
                .toList();

        if (compatible.isEmpty()) {
            throw new MockerException("No method found for " + methodName + " compatible with args " + Arrays.deepToString(argsFilter));
        }

        List<Method> unique = deduplicate(compatible);
        if (unique.size() > 1) {
            throw new MockerException("Ambiguous InOrder verification for method '" + methodName + "' with args. Candidates: " + unique);
        }
        this.method = unique.get(0);
    }

    private List<Method> deduplicate(List<Method> methods) {
        List<Method> unique = new ArrayList<>();
        for (Method m : methods) {
            boolean duplicate = false;
            for (Method existing : unique) {
                if (Arrays.equals(m.getParameterTypes(), existing.getParameterTypes())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                unique.add(m);
            }
        }
        return unique;
    }

    private static Method resolveMethodStrict(Object mock, String methodName, Class<?>... paramTypes) {
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            try { return clazz.getDeclaredMethod(methodName, paramTypes); } catch (NoSuchMethodException e) {}
            clazz = clazz.getSuperclass();
        }
        throw new MockerException("Method not found: " + methodName + " with params " + Arrays.toString(paramTypes));
    }

    private static List<Method> collectCandidates(Object mock, String methodName) {
        List<Method> candidates = new ArrayList<>();
        Class<?> clazz = mock.getClass();
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) candidates.add(m);
            }
            clazz = clazz.getSuperclass();
        }
        for (Method m : mock.getClass().getMethods()) {
            if (m.getName().equals(methodName)) candidates.add(m);
        }
        return candidates;
    }
}
