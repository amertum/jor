package com.google.code.jor;

import static com.google.code.jor.ProxyUtils.newProxyThreadLoader;
import static java.lang.Thread.currentThread;
import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.Test;

/**
 * Unit test of {@link ProxyUtils}.
 */
public class ProxyUtilsTest {

    @Test
    public final void testNewProxyThreadLoader()
    {
        final List<String> list = (List) Proxy.newProxyInstance(
                currentThread().getContextClassLoader(),
                new Class<?>[] {
                    List.class
                },
                new InvocationHandler() {

                    @Override
                    public Object invoke(
                            final Object proxy,
                            final Method method,
                            final Object[] args)
                        throws Throwable
                    {
                        assertThat(method.getName()).isEqualTo("add");
                        assertThat(args[0]).isEqualTo("one");
                        return true;
                    }
                });

        assertThat(list.add("one")).isTrue();
    }


    @Test
    public final void testNewProxyThreadLoader_withOneInterface()
    {
        final List<String> list = newProxyThreadLoader(new InvocationHandler() {

            @Override
            public Object invoke(
                    final Object proxy,
                    final Method method,
                    final Object[] args)
                throws Throwable
            {
                assertThat(method.getName()).isEqualTo("add");
                assertThat(args[0]).isEqualTo("one");
                return true;
            }
        }, List.class);

        assertThat(list.add("one")).isTrue();
    }


    @Test
    public final void testNewProxyThreadLoader_withManyInterfaces()
    {
        final List<String> list = newProxyThreadLoader(new InvocationHandler() {

            @Override
            public Object invoke(
                    final Object proxy,
                    final Method method,
                    final Object[] args)
                throws Throwable
            {
                this.i++;

                if (this.i == 1) {
                    assertThat(method.getName()).isEqualTo("add");
                    assertThat(args[0]).isEqualTo("one");
                    return true;
                }
                else if (this.i == 2) {
                    assertThat(method.getName()).isEqualTo("compareTo");
                    assertThat(args[0]).isEqualTo("string");
                    return 1;
                }

                throw new UnsupportedOperationException("no invocation");
            }

            private int i = 0;

        }, List.class, Comparable.class, Cloneable.class);

        assertThat(list.add("one")).isTrue();
        assertThat(((Comparable) list).compareTo("string")).isEqualTo(1);
    }

}
