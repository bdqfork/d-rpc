package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RemoteException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.util.IdUtils;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import cn.bdqfork.rpc.protocol.RpcResponse;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-03-02
 */
public abstract class AbstractProxyInstanceGenerator<T> implements ProxyInstanceGenerator<T> {
    private static final String TO_STRING_METHOD = "toString";
    private static final String EQUALS_METHOD = "equals";
    private static final String HASHCODE_METHOD = "hashCode";

    private Invoker<RpcResponse> invoker;
    private Class<?> serviceInterface;
    private String refName;

    public AbstractProxyInstanceGenerator(Invoker<RpcResponse> invoker, Class<?> serviceInterface, String refName) {
        this.invoker = invoker;
        this.serviceInterface = serviceInterface;
        this.refName = refName;
    }

    public Invoker<RpcResponse> getInvoker() {
        return invoker;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public String getRefName() {
        return refName;
    }

    protected Object doInvoke(Method method, Object[] args) throws RpcException {

        Object result = doObjectMethod(method, args);

        if (result != null) {
            return result;
        }

        Invocation invocation = buildInvocation(method, args);

        RpcResponse rpcResponse = getInvoker().invoke(invocation);
        if (rpcResponse.getException() != null) {
            throw new RemoteException(rpcResponse.getMessage(), rpcResponse.getException());
        }
        return rpcResponse.getData();
    }

    private Object doObjectMethod(Method method, Object[] args) {
        if (TO_STRING_METHOD.equals(method.getName())) {
            return invoker.toString();
        }
        if (EQUALS_METHOD.equals(method.getName())) {
            return invoker.equals(args[0]);
        }
        if (HASHCODE_METHOD.equals(method.getName())) {
            return invoker.hashCode();
        }
        return null;
    }

    private Invocation buildInvocation(Method method, Object[] args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Invocation invocation = new Invocation(IdUtils.getUUID(), serviceInterface.getName(), refName, method.getName());
        if (parameterTypes.length != 0) {
            invocation.setParameterTypes(parameterTypes);
            invocation.setArguments(args);
        }
        return invocation;
    }
}
