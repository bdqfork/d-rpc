package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.Invoker;

/**
 * @author bdq
 * @since 2019/9/30
 */
public class JavassistProxyFactory extends AbstractProxyFactory {
    public static final String NAME = "javassist";

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Invoker<T> invoker) {
        Class<T> clazz = invoker.getInterface();
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvokerInvocationHandler(invoker));
    }

}
