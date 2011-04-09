package com.google.code.jor;

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Method;
import java.util.List;

public class Invocation<Type> {

    Invocation(
            final Type instance,
            final Method method,
            final List<? extends Object> arguments)
    {
        this.instance = instance;
        this.method = method;
        this.arguments = unmodifiableList(arguments);
    }


    public Type getInstance()
    {
        return this.instance;
    }


    public Method getMethod()
    {
        return this.method;
    }


    public List<? extends Object> getArguments()
    {
        return this.arguments;
    }

    private final Type instance;

    private final Method method;

    private final List<? extends Object> arguments;

}
