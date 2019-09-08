package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-08-26
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private static final String TO_STRING_METHOD = "toString";
    private static final String EQUALS_METHOD = "equals";
    private static final String HASHCODE_METHOD = "hashCode";

    private Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        RpcInvocation rpcInvocation = new RpcInvocation(method.getName(), method.getParameterTypes(), args);
        return invoker.invoke(rpcInvocation).getData();
    }

}
