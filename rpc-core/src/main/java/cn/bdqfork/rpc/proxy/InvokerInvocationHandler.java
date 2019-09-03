package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.Invoker;
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
        Object result = doObjectMethod(method, args);
        if (result != null) {
            return result;
        }
        RpcInvocation rpcInvocation = new RpcInvocation(method, args);
        return invoker.invoke(rpcInvocation).getData();
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
}
