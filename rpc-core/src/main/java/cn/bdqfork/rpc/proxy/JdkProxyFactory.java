package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.AbstractInvoker;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @since 2019-02-15
 */
public class JdkProxyFactory implements ProxyFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Invoker<T> invoker) {
        Class<T> clazz = invoker.getInterface();
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url){
        return new AbstractInvoker<T>(proxy, type, url) {
            @Override
            protected Result doInvoke(Invocation invocation) throws RpcException {
                Method method;
                try {
                    method = type.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    throw new RpcException(e);
                }
                Object result;
                try {
                    result = method.invoke(proxy, invocation.getArguments());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return new Result(e.getMessage(), e);
                }
                return new Result(result);
            }
        };
    }
}
