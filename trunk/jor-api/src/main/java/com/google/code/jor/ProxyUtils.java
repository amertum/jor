package com.google.code.jor;

import static java.lang.Thread.currentThread;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

final class ProxyUtils {

    private ProxyUtils()
    {
        // not instantiable
    }


    public static <Type> Type newProxyThreadLoader(
            final InvocationHandler invocationHandler,
            final Class<Type> mainInterface,
            final Class<?>... otherInterfaces)
    {
        final int interfaceCount = (otherInterfaces != null)
                ? otherInterfaces.length + 1
                : 1;
        final Class<?>[] interfaces = new Class<?>[interfaceCount];
        interfaces[0] = mainInterface;
        if (otherInterfaces != null) {
            System.arraycopy(otherInterfaces, 0, interfaces, 1, otherInterfaces.length);
        }

        final Type proxy = (Type) Proxy.newProxyInstance(
                currentThread().getContextClassLoader(),
                interfaces,
                invocationHandler);

        return proxy;
    }

}
