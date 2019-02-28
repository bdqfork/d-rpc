package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RemoteException;
import cn.bdqfork.common.util.IdUtils;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.invoker.Invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class JdkInvocationHandler implements InvocationHandler {
    private Invoker<Object> invoker;
    private Class<?> serviceInterface;
    private String refName;

    public JdkInvocationHandler(Invoker<Object> invoker, Class<?> serviceInterface, String refName) {
        this.invoker = invoker;
        this.serviceInterface = serviceInterface;
        this.refName = refName;
    }

    public Object newProxyInstance() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return invoker.toString();
        }
        if ("equals".equals(method.getName())) {
            return invoker.equals(args[0]);
        }
        if ("hashCode".equals(method.getName())) {
            return invoker.hashCode();
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Invocation invocation = new Invocation(IdUtils.getUUID(), proxy.getClass().getName(), refName, method.getName());
        if (parameterTypes.length != 0) {
            invocation.setParameterTypes(parameterTypes);
            invocation.setArguments(args);
        }
        RpcResponse rpcResponse = (RpcResponse) invoker.invoke(invocation);
        if (rpcResponse.getException() != null) {
            throw new RemoteException(rpcResponse.getMessage(), rpcResponse.getException());
        }
        return rpcResponse.getData();
    }
}
