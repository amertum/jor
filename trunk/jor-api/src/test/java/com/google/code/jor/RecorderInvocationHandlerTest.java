package com.google.code.jor;

import static com.google.code.jor.ProxyUtils.newProxyThreadLoader;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;

/**
 * Unit test of {@link RecorderInvocationHandler}.
 */
public class RecorderInvocationHandlerTest {

    @Test
    public final void testRecording()
    {
        final RecorderInvocationHandler invocationHandler = new RecorderInvocationHandler(null);

        final List<String> proxyList = newProxyThreadLoader(invocationHandler, List.class);
        assert_that_invocation_are_recorded(proxyList, invocationHandler);
    }


    @Test
    public final void testRecordingWithDelegate()
        throws Throwable
    {
        final InvocationHandler delegate = mock(InvocationHandler.class);
        final RecorderInvocationHandler invocationHandler = new RecorderInvocationHandler(delegate);

        final List<String> proxyList = newProxyThreadLoader(invocationHandler, List.class);
        assert_that_invocation_are_recorded(proxyList, invocationHandler);

        verify(delegate, times(3)).invoke(anyObject(), any(Method.class), Matchers.any(Object[].class));
    }


    @Test
    public final void testReverserRecording()
        throws Exception
    {
        final RecorderInvocationHandler invocationHandler = new RecorderInvocationHandler(null);

        final List<String> proxyList = newProxyThreadLoader(invocationHandler, List.class);
        assert_that_reverser_with_invocation_are_recorded(proxyList, invocationHandler);
    }


    private static void assert_that_invocation_are_recorded(
            final List<String> proxyList,
            final RecorderInvocationHandler invocationHandler)
    {
        proxyList.add(1, "one");
        proxyList.get(1);
        proxyList.clear();

        assertThat(invocationHandler.getRecordings()).hasSize(3);

        assertThat(invocationHandler.getRecordings().get(0).getMethod().getName()).isEqualTo("add");
        assertThat(invocationHandler.getRecordings().get(0).getArguments()).hasSize(2);
        assertThat(invocationHandler.getRecordings().get(0).getArguments().get(0)).isEqualTo(1);
        assertThat(invocationHandler.getRecordings().get(0).getArguments().get(1)).isEqualTo("one");

        assertThat(invocationHandler.getRecordings().get(1).getMethod().getName()).isEqualTo("get");
        assertThat(invocationHandler.getRecordings().get(1).getArguments()).hasSize(1);
        assertThat(invocationHandler.getRecordings().get(1).getArguments().get(0)).isEqualTo(1);

        assertThat(invocationHandler.getRecordings().get(2).getMethod().getName()).isEqualTo("clear");
        assertThat(invocationHandler.getRecordings().get(2).getArguments()).hasSize(0);
    }


    private static void assert_that_reverser_with_invocation_are_recorded(
            final List<String> proxyList,
            final RecorderInvocationHandler invocationHandler)
        throws Exception
    {
        final Reverser<Object> reverser1 = mock(Reverser.class);
        final Reverser<Object> reverser2 = mock(Reverser.class);
        final Reverser<Object> reverser3 = mock(Reverser.class);

        invocationHandler.setNextInvocationReverser(reverser1);
        proxyList.add(1, "one");
        invocationHandler.setNextInvocationReverser(reverser2);
        proxyList.get(1);
        invocationHandler.setNextInvocationReverser(reverser3);
        proxyList.clear();

        assertThat(invocationHandler.getReversers().get(List.class.getMethod("add", int.class, Object.class))).isSameAs(
                reverser1);
        assertThat(invocationHandler.getReversers().get(List.class.getMethod("get", int.class))).isSameAs(reverser2);
        assertThat(invocationHandler.getReversers().get(List.class.getMethod("clear"))).isSameAs(reverser3);

        proxyList.add(2, "two");
        proxyList.get(2);
        proxyList.clear();

        assertThat(invocationHandler.getRecordings().get(0).getReverser()).isSameAs(reverser1);
        assertThat(invocationHandler.getRecordings().get(1).getReverser()).isSameAs(reverser2);
        assertThat(invocationHandler.getRecordings().get(2).getReverser()).isSameAs(reverser3);
    }

}
