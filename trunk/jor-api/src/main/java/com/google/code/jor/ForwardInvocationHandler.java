package com.google.code.jor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ForwardInvocationHandler<Type>
        implements InvocationHandler {

    public ForwardInvocationHandler(
            final Type forwardTo)
    {
        if (forwardTo == null) {
            throw new NullPointerException("forwardTo must not be null");
        }

        this.forwardTo = forwardTo;
    }


    @Override
    public Object invoke(
            final Object proxy,
            final Method method,
            final Object[] args)
        throws Throwable
    {
        final Method forwardMethod = this.forwardTo.getClass().getMethod(method.getName(), method.getParameterTypes());
        final Object result = forwardMethod.invoke(this.forwardTo, args);

        return result;
    }

    private final Type forwardTo;

}
