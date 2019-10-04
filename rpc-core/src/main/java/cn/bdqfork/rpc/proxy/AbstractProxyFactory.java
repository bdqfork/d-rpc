package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.rpc.context.AbstractInvoker;
import cn.bdqfork.rpc.context.ResponseResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/9/30
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
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
                    return new ResponseResult(e.getMessage(), e);
                }
                return new ResponseResult(result);
            }
        };
    }
}
