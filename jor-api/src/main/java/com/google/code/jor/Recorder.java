package com.google.code.jor;

import static com.google.code.jor.ProxyUtils.newProxyThreadLoader;

import java.util.List;

public class Recorder<Type> {

    Recorder(
            final Class<Type> classToRecord)
    {
        this.classToRecord = classToRecord;
    }


    public Type instance()
    {
        this.recorder = new RecorderInvocationHandler();
        final Type proxy = newProxyThreadLoader(this.recorder, this.classToRecord, Recordable.class);

        return proxy;
    }


    public Type instance(
            final Type forwardTo)
    {
        this.recorder = new RecorderInvocationHandler(new ForwardInvocationHandler<Type>(forwardTo));
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

    private final Class<Type> classToRecord;

    private RecorderInvocationHandler recorder;

}
