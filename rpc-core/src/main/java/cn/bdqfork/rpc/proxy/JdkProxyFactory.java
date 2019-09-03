package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.ServiceInvoker;
import cn.bdqfork.rpc.registry.URL;

import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @since 2019-02-15
 */
public class JdkProxyFactory implements ProxyFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Invoker invoker) throws RpcException {
        Class<?> clazz = invoker.getInterface();
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return new ServiceInvoker<>(proxy, type, url);
    }
}
