package com.google.code.jor;

import static com.google.code.jor.ProxyUtils.newProxyThreadLoader;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit test of {@link ForwardInvocationHandler}.
 */
public class ForwardInvocationHandlerTest {

    @Test
    public final void testInvoke()
    {
        final List<String> myList = new ArrayList<String>();

        final List<String> proxyList = newProxyThreadLoader(
                new ForwardInvocationHandler(myList),
                List.class);
        proxyList.add("one");
        assertThat(myList).containsExactly("one");
        proxyList.add("two");
        assertThat(myList).containsExactly("one", "two");
        assertThat(proxyList.get(0)).isEqualTo("one");
        proxyList.clear();
        assertThat(myList).isEmpty();
    }

}
