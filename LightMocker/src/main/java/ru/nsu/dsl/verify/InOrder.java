package ru.nsu.dsl.verify;

import ru.nsu.core.model.Invocation;
import ru.nsu.core.state.MockState;
import ru.nsu.exception.MockerException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class InOrder {
    private final Set<Object> monitoredMocks;
    private int cursor = 0;

    public InOrder(Object... mocks) {
        if (mocks == null || mocks.length == 0) {
            throw new MockerException("InOrder requires at least one mock");
        }
        this.monitoredMocks = new HashSet<>(Arrays.asList(mocks));
    }

    public InOrderVerificationBuilder verify(Object mock, String methodName, Class<?>... paramTypes) {
        if (!monitoredMocks.contains(mock)) {
            throw new MockerException("Mock not monitored by this InOrder instance");
        }
        return new InOrderVerificationBuilder(this, mock, methodName, paramTypes);
    }

    /**
     * Finds the next matching invocation in global history starting from cursor.
     * Advances cursor on success.
     */
    protected void verifyCall(Object mock, java.lang.reflect.Method method, Object[] argsFilter) {
        List<Invocation> globalTrace = MockState.getInstance().getInvocationRegistry().getGlobalInvocations();
        
        for (int i = cursor; i < globalTrace.size(); i++) {
            Invocation invocation = globalTrace.get(i);
            
            // Check if this invocation belongs to any monitored mock
            if (!monitoredMocks.contains(invocation.getMockInstance())) {
                continue; // Skip calls to other mocks
            }

            // Check if it's the specific call we are looking for
            if (invocation.getMockInstance() == mock && 
                invocation.getMethod().getName().equals(method.getName()) &&
                Arrays.equals(invocation.getMethod().getParameterTypes(), method.getParameterTypes())) {
                
                if (argsFilter != null && !Arrays.deepEquals(argsFilter, invocation.getArgs())) {
                    // Method matches, but arguments don't. In Mockito, this would fail InOrder
                    // if this was the NEXT expected call. 
                    // Let's stick to simple logic: find NEXT matching call.
                    continue; 
                }
                
                // Success! Advance cursor
                cursor = i + 1;
                return;
            }
        }

        throw new MockerException("InOrder verification failed for method: " + method.getName() + 
                                  " on " + mock.getClass().getSimpleName() + 
                                  (argsFilter != null ? " with args " + Arrays.toString(argsFilter) : ""));
    }
}
