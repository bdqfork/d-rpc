package cn.bdqfork.rpc.invoker;

import cn.bdqfork.rpc.netty.RpcResponse;

/**
 * @author bdq
 * @date 2019-02-20
 */
public interface Provider {
    
    void register(String serviceInterface, Object instance);

    Object lookup(String serviceInterface);

    RpcResponse doInvoke(Invocation request);
}
