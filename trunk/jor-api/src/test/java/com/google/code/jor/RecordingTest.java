package com.google.code.jor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test of {@link Recording}.
 */
public class RecordingTest {

    @Test
    public final void testReplayOn()
        throws Exception
    {
        final Method method = List.class.getMethod("add", Object.class);
        final List<String> arguments = Arrays.asList("one");

        final List<String> myList = new ArrayList<String>();
        final Recording recording = new Recording(method, arguments);
        recording.replayOn(myList);

        assertThat(myList).containsExactly("one");
    }


    @Test
    public final void testReverseOn()
        throws Exception
    {
        final Reverser<Object> reverser = mock(Reverser.class);

        final Method method = List.class.getMethod("add", Object.class);
        final List<String> arguments = Arrays.asList("one");

        final List<String> myList = new ArrayList<String>();
        final Recording recording = new Recording(method, arguments, reverser);
        recording.reverseOn(myList);

        verify(reverser).reverse(any(Invocation.class));
        assertThat(recording.getReverser()).isSameAs(reverser);
    }

}
