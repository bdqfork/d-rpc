package cn.bdqfork.rpc.netty.consumer.context;

import cn.bdqfork.rpc.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class RpcContextManager {
    private static Map<String, RpcContext> futureMap = new ConcurrentHashMap<>();

    public static void registerContext(RpcContext rpcContext) {
        futureMap.put(rpcContext.getRequestId(), rpcContext);
    }

    public static void removeContext(String requestId) {
        futureMap.remove(requestId);
    }

    public static void doReceived(RpcResponse rpcResponse) {
        RpcContext context = futureMap.get(rpcResponse.getRequestId());
        if (context != null) {
            DefaultFuture<RpcResponse> future = context.getFuture();
            future.setResult(rpcResponse);
            removeContext(rpcResponse.getRequestId());
        }
    }
}
