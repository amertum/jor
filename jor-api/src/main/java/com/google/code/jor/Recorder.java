package com.google.code.jor;

import static com.google.code.jor.ProxyUtils.newProxyThreadLoader;

import java.util.List;

public class Recorder<Type> {

    Recorder(
            final Class<Type> classToRecord)
    {
        this(classToRecord, null);
    }


    Recorder(
            final Class<Type> classToRecord,
            final Type forwardTo)
    {
        this.classToRecord = classToRecord;
        this.forwardTo = forwardTo;
    }


    public Type instance()
    {
        this.recorder = makeRecorder(this.forwardTo);
        final Type proxy = newProxyThreadLoader(this.recorder, this.classToRecord, Recordable.class);

        return proxy;
    }


    public void replayOn(
            final Type objectToReplayOn)
    {
        for (final Recording recording : this.getRecordings()) {
            recording.replayOn(objectToReplayOn);
        }
    }


    public List<Recording> getRecordings()
    {
        return this.recorder.getRecordings();
    }


    public static boolean isRecordable(
            final Object object)
    {
        return (object instanceof Recordable);
    }


    private static <Type> RecorderInvocationHandler makeRecorder(
            final Type forwardTo)
    {
        final RecorderInvocationHandler rec = (forwardTo == null)
                ? new RecorderInvocationHandler()
                : new RecorderInvocationHandler(new ForwardInvocationHandler<Type>(forwardTo));

        return rec;
    }

    private final Class<Type> classToRecord;

    private final Type forwardTo;

    private RecorderInvocationHandler recorder;

}
