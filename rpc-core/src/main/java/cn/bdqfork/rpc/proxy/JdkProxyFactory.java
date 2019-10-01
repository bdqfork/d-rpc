package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.Invoker;

import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @since 2019-02-15
 */
public class JdkProxyFactory extends AbstractProxyFactory {
    public static final String NAME = "jdk";

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Invoker<T> invoker) {
        Class<T> clazz = invoker.getInterface();
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvokerInvocationHandler(invoker));
    }

}
