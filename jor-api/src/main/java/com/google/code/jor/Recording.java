package com.google.code.jor;

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Recording {

    Recording(
            final Method method,
            final List<? extends Object> arguments)
    {
        this.method = method;
        this.arguments = new ArrayList<Object>(arguments);
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


    public Method getMethod()
    {
        return this.method;
    }


    public List<Object> getArguments()
    {
        return unmodifiableList(this.arguments);
    }

    private final Method method;

    private final List<Object> arguments;

}
