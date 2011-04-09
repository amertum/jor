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
        if (this.recorder == null) {
            this.recorder = makeRecorder(this.forwardTo);
        }

        if (this.recorder.getNextInvocationReverser() != null) {
            throw new IllegalStateException(
                    "It appears that addReverse() had been called without invoking the method to reverse. ie : addReverse(someReverser).methodToReverse()");
        }

        final Type proxy = newProxyThreadLoader(this.recorder, this.classToRecord, Recordable.class);

        return proxy;
    }


    public Type addReverse(
            final Reverser reverser)
    {
        if (this.recorder != null) {
            throw new IllegalStateException("all addReverse() settings must be called before instance()");
        }

        final Reverser nonNullReverser = (reverser == null)
                ? new NullReverser()
                : reverser;
        this.recorder = makeRecorder(this.forwardTo);
        this.recorder.setNextInvocationReverser(nonNullReverser);

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


    public void reverseOn(
            final Type objectToReplayOn)
    {
        for (final Recording recording : this.getRecordings()) {
            recording.reverseOn(objectToReplayOn);
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
