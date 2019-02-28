package cn.bdqfork.rpc.provider.invoker;

import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.provider.LocalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class RemoteInvoker implements Invoker<RpcResponse> {
    private static final Logger log = LoggerFactory.getLogger(RemoteInvoker.class);
    private LocalRegistry localRegistry;

    public RemoteInvoker(LocalRegistry localRegistry) {
        this.localRegistry = localRegistry;
    }

    @Override
    public RpcResponse invoke(Invocation invocation) {
        Object instance = localRegistry.lookup(invocation.getRefName());
        Class<?> clazz = instance.getClass();
        try {
            Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object result = method.invoke(instance, invocation.getArguments());
            return new RpcResponse(invocation.getRequestId(), result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            return new RpcResponse(invocation.getRequestId(), e.getMessage(), e);
        }
    }
}
