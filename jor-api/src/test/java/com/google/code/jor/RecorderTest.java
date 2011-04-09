package com.google.code.jor;

import static com.google.code.jor.Recorder.isRecordable;
import static com.google.code.jor.Types.type;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Functional test of {@link Recorder}.
 */
public class RecorderTest {

    @Test
    public void testRecordOnClass()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        final List<String> list = recorder.instance();

        assertThat(isRecordable(list)).isTrue();

        list.add("one");
        list.add("two");
        list.get(1);
        list.clear();

        assertThat(recorder.getRecordings().get(0).getMethod().getName()).isEqualTo("add");
        assertThat(recorder.getRecordings().get(0).getArguments().get(0)).isEqualTo("one");

        assertThat(recorder.getRecordings().get(1).getMethod().getName()).isEqualTo("add");
        assertThat(recorder.getRecordings().get(1).getArguments().get(0)).isEqualTo("two");

        assertThat(recorder.getRecordings().get(2).getMethod().getName()).isEqualTo("get");
        assertThat(recorder.getRecordings().get(2).getArguments().get(0)).isEqualTo(1);

        assertThat(recorder.getRecordings().get(3).getMethod().getName()).isEqualTo("clear");
        assertThat(recorder.getRecordings().get(3).getArguments().size()).isEqualTo(0);
    }


    @Test
    public void testRecordOnObject()
    {
        final List<String> myList = new ArrayList<String>();
        final Recorder<List<String>> recorder = new Recorder(List.class, myList);
        final List<String> list = recorder.instance();

        assertThat(isRecordable(list)).isTrue();

        list.add("one");
        assertThat(myList).containsExactly("one");
        list.add("two");
        assertThat(myList).containsExactly("one", "two");
        list.get(1);
        list.clear();
        assertThat(myList).isEmpty();

        assertThat(recorder.getRecordings().get(0).getMethod().getName()).isEqualTo("add");
        assertThat(recorder.getRecordings().get(0).getArguments().get(0)).isEqualTo("one");

        assertThat(recorder.getRecordings().get(1).getMethod().getName()).isEqualTo("add");
        assertThat(recorder.getRecordings().get(1).getArguments().get(0)).isEqualTo("two");

        assertThat(recorder.getRecordings().get(2).getMethod().getName()).isEqualTo("get");
        assertThat(recorder.getRecordings().get(2).getArguments().get(0)).isEqualTo(1);

        assertThat(recorder.getRecordings().get(3).getMethod().getName()).isEqualTo("clear");
        assertThat(recorder.getRecordings().get(3).getArguments().size()).isEqualTo(0);

    }


    @Test
    public void testReplayFromRecording()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        final List<String> list = recorder.instance();

        list.add("one");
        list.add("two");
        list.get(1);
        list.clear();

        final List<String> myList = new ArrayList<String>();
        recorder.getRecordings().get(0).replayOn(myList);
        assertThat(myList).containsExactly("one");
        recorder.getRecordings().get(1).replayOn(myList);
        assertThat(myList).containsExactly("one", "two");
        recorder.getRecordings().get(2).replayOn(myList);
        recorder.getRecordings().get(3).replayOn(myList);
        assertThat(myList).isEmpty();
    }


    @Test
    public void testReplayFromRecorder()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        final List<String> list = recorder.instance();

        list.add("one");
        list.add("two");

        final List<String> myList = new ArrayList<String>();
        recorder.replayOn(myList);
        assertThat(myList).containsExactly("one", "two");
    }


    @Test
    public void testReverseFromRecording()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        recorder.addReverse(new Reverser<List<String>>() {
            @Override
            public void reverse(
                    final Invocation<List<String>> invocation)
            {
                invocation.getInstance().remove(invocation.getArguments().get(0));
            }
        }).add(Types.type(String.class));

        final List<String> list = recorder.instance();
        list.add("one");
        list.add("two");

        final List<String> myList = new ArrayList<String>();
        recorder.replayOn(myList);
        assertThat(myList).containsExactly("one", "two");

        recorder.getRecordings().get(0).reverseOn(myList);
        assertThat(myList).containsExactly("two");
    }


    @Test
    public void testAddReverseAndForgotProxyCall()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        recorder.addReverse(new Reverser<List<String>>() {
            @Override
            public void reverse(
                    final Invocation<List<String>> invocation)
            {
                invocation.getInstance().remove(invocation.getArguments().get(0));
            }
        }); // forgot .add(Types.type(String.class));

        try {
            recorder.instance();
            Assert.fail("must throws " + IllegalStateException.class);
        }
        catch (final IllegalStateException e) {
        }
    }


    @Test
    public void testReverseFromRecorder()
    {
        final Recorder<List<String>> recorder = new Recorder(List.class);
        recorder.addReverse(new Reverser<List<String>>() {
            @Override
            public void reverse(
                    final Invocation<List<String>> invocation)
            {
                invocation.getInstance().remove(invocation.getArguments().get(0));
            }
        }).add(type(String.class));

        final List<String> list = recorder.instance();
        list.add("one");
        list.add("two");

        final List<String> myList = new ArrayList<String>();
        recorder.replayOn(myList);
        assertThat(myList).containsExactly("one", "two");

        recorder.reverseOn(myList);
        assertThat(myList).isEmpty();
    }


    @Test
    public void testIsRecordable()
    {
        assertThat(isRecordable(new Recorder<List>(List.class).instance())).isTrue();
        assertThat(isRecordable(new Recorder<List>(List.class, new ArrayList<String>()).instance())).isTrue();

        assertThat(isRecordable(List.class)).isFalse();
        assertThat(isRecordable(new ArrayList<String>())).isFalse();
    }

}
