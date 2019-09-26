package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.context.result.AsyncResult;
import cn.bdqfork.rpc.context.RpcInvocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-08-26
 */
public class InvokerInvocationHandler implements InvocationHandler {

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
        Result result = invoker.invoke(rpcInvocation);
        if (result.hasException()) {
            throw result.getException();
        }
        return result.getValue();
    }

}
