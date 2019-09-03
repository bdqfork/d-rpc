package cn.bdqfork.rpc;

import cn.bdqfork.rpc.remote.context.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-08-23
 */
public class RpcInvocation implements Invocation {
    private static final Logger log = LoggerFactory.getLogger(RpcInvocation.class);

    private Method method;
    private Object[] args;

    private RpcContext rpcContext;

    public RpcInvocation(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public void setRpcContext(RpcContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    public RpcContext getRpcContext() {
        return rpcContext;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
