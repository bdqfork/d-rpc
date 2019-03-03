package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.protocol.invoker.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class JdkProxyInstanceGenerator<T> extends AbstractProxyInstanceGenerator<T> implements InvocationHandler {

    public JdkProxyInstanceGenerator(Invoker<Object> invoker, Class<?> serviceInterface, String refName) {
        super(invoker, serviceInterface, refName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newProxyInstance() {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{getServiceInterface()}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doInvoke(method, args);
    }
}
