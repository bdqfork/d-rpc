package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.RpcContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-08-27
 */
public class ServiceInvoker<T> implements Invoker<T> {
    private T proxy;
    private Class<T> type;
    private URL url;

    public ServiceInvoker(T proxy, Class<T> type, URL url) {
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public RpcResponse invoke(Invocation invocation) throws RpcException {
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        RpcContext rpcContext = invocation.getRpcContext();
        Method method = rpcInvocation.getMethod();
        Object result;
        try {
            result = method.invoke(proxy, rpcContext.getArguments());
        } catch (IllegalAccessException | InvocationTargetException e) {
            return new RpcResponse(rpcContext.getRequestId(), e.getMessage(), e);
        }
        return new RpcResponse(rpcContext.getRequestId(), result);
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public URL getUrl() {
        return url;
    }

}
