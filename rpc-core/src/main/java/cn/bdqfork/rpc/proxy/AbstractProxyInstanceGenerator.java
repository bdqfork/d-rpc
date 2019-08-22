package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RemoteException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.util.IdUtils;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.RpcContext;
import cn.bdqfork.rpc.remote.invoker.Invoker;

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

        RpcContext rpcContext = buildRpcContext(method, args);

        RpcResponse rpcResponse = getInvoker().invoke(rpcContext.getContext());
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

    private RpcContext buildRpcContext(Method method, Object[] args) {
        RpcContext rpcContext = new RpcContext(IdUtils.getUUID());

        RpcContext.Context context = rpcContext.getContext();
        Class<?>[] parameterTypes = method.getParameterTypes();

        context.setServiceInterface(serviceInterface.getName());
        context.setRefName(refName);
        context.setMethodName(method.getName());

        if (parameterTypes.length != 0) {
            context.setParameterTypes(parameterTypes);
            context.setArguments(args);
        }
        return rpcContext;
    }

}
