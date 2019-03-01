package cn.bdqfork.rpc.consumer.context;

import cn.bdqfork.rpc.consumer.remote.Exchanger;
import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.netty.DefaultFuture;
import cn.bdqfork.rpc.netty.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class RpcContext {
    private static Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    public void addContext(Invocation invocation, DefaultFuture future) {
        futureMap.put(invocation.getRequestId(), future);
    }

    public static void doReceived(RpcResponse rpcResponse) {
        DefaultFuture future = futureMap.get(rpcResponse.getRequestId());
        future.setResult(rpcResponse);
    }

}
