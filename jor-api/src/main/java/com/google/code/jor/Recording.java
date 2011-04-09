package com.google.code.jor;

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Recording {

    Recording(
            final Method method,
            final List<? extends Object> arguments,
            final Reverser<Object> reverser)
    {
        this.method = method;
        this.arguments = new ArrayList<Object>(arguments);
        this.reverser = reverser;
    }


    Recording(
            final Method method,
            final List<? extends Object> arguments)
    {
        this(method, arguments, null);
    }


    public Object replayOn(
            final Object objectToReplayOn)
    {
        try {
            return this.getMethod().invoke(objectToReplayOn, this.getArguments().toArray());
        }
        catch (final IllegalArgumentException e) {
            throw new UnsupportedOperationException(e);
        }
        catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        }
        catch (final InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }


    public void reverseOn(
            final Object objectToReverseOn)
    {
        if (this.getReverser() == null) {
            throw new UnsupportedOperationException("there is no reverser (null)");
        }

        this.getReverser().reverse(new Invocation<Object>(objectToReverseOn, this.getMethod(), this.getArguments()));
    }


    public Method getMethod()
    {
        return this.method;
    }


    public List<Object> getArguments()
    {
        return unmodifiableList(this.arguments);
    }


    public Reverser<Object> getReverser()
    {
        return this.reverser;
    }

    private final Method method;

    private final List<Object> arguments;

    private final Reverser<Object> reverser;

}
