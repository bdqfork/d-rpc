package cn.bdqfork.rpc.provider;

import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class RpcRemoteInvoker implements Invoker<RpcResponse> {
    private static final Logger log = LoggerFactory.getLogger(RpcRemoteInvoker.class);

    @Override
    public RpcResponse invoke(Invocation invocation) {
        Object instance = LocalRegistry.lookup(invocation.getServiceInterface());
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
