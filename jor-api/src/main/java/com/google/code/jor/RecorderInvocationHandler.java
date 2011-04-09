package com.google.code.jor;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RecorderInvocationHandler
        implements InvocationHandler {

    public RecorderInvocationHandler(
            final InvocationHandler delegate)
    {
        this.delegate = delegate;
    }


    public RecorderInvocationHandler()
    {
        this(null);
    }


    @Override
    public Object invoke(
            final Object proxy,
            final Method method,
            final Object[] args)
        throws Throwable
    {
        final List<Object> arguments = (args == null)
                ? new ArrayList<Object>()
                : Arrays.asList(args);

        if (this.nextInvocationReverser == null) {
            final Recording recording = new Recording(method, arguments, this.reversers.get(method));
            this.recordings.add(recording);
        }
        else {
            this.reversers.put(method, this.nextInvocationReverser);
            this.nextInvocationReverser = null;
        }

        final Object result;
        if (this.delegate == null) {
            if (method.getReturnType().isPrimitive()) {
                result = PRIMITIVE_VALUES.get(method.getReturnType());
            }
            else {
                result = null;
            }
        }
        else {
            result = this.delegate.invoke(proxy, method, args);
        }

        return result;
    }


    public Reverser<Object> getNextInvocationReverser()
    {
        return this.nextInvocationReverser;
    }


    public void setNextInvocationReverser(
            final Reverser<Object> reverser)
    {
        this.nextInvocationReverser = reverser;
    }


    public List<Recording> getRecordings()
    {
        return unmodifiableList(this.recordings);
    }


    public Map<Method, Reverser<Object>> getReversers()
    {
        return unmodifiableMap(this.reversers);
    }

    private final InvocationHandler delegate;

    private final List<Recording> recordings = new ArrayList<Recording>();

    private final Map<Method, Reverser<Object>> reversers = new HashMap<Method, Reverser<Object>>();

    private Reverser<Object> nextInvocationReverser;

    private static final Map<Class<?>, Object> PRIMITIVE_VALUES = new HashMap<Class<?>, Object>();

    static {
        PRIMITIVE_VALUES.put(boolean.class, false);
        PRIMITIVE_VALUES.put(char.class, (char) 0);
        PRIMITIVE_VALUES.put(byte.class, (byte) 0);
        PRIMITIVE_VALUES.put(short.class, (short) 0);
        PRIMITIVE_VALUES.put(int.class, 0);
        PRIMITIVE_VALUES.put(long.class, 0L);
        PRIMITIVE_VALUES.put(float.class, 0F);
        PRIMITIVE_VALUES.put(double.class, 0D);
    }

}
