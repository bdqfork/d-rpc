package cn.bdqfork.rpc.netty.consumer.context;

import cn.bdqfork.rpc.protocol.RpcResponse;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class RpcContext {

    private String requestId;
    private DefaultFuture<RpcResponse> future;

    public RpcContext(String requestId, DefaultFuture<RpcResponse> future) {
        this.requestId = requestId;
        this.future = future;
    }

    public String getRequestId() {
        return requestId;
    }

    public DefaultFuture<RpcResponse> getFuture() {
        return future;
    }
}