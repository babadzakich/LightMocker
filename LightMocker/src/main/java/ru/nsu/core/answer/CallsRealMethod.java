package ru.nsu.core.answer;

import ru.nsu.core.model.Invocation;
import java.lang.reflect.Method;

public class CallsRealMethod<R> implements Answer<R> {
    private final Object realObject;

    public CallsRealMethod(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R answer(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        method.setAccessible(true);
        return (R) method.invoke(realObject, invocation.getArgs());
    }
}