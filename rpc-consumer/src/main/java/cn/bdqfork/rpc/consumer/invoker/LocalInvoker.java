package cn.bdqfork.rpc.consumer.invoker;

import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.DefaultFuture;
import cn.bdqfork.rpc.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class LocalInvoker implements Invoker<Object> {
    private static final Logger log = LoggerFactory.getLogger(LocalInvoker.class);
    private NettyClient nettyClient;
    private long timeout;

    public LocalInvoker(NettyClient nettyClient, long timeout) {
        this.nettyClient = nettyClient;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Invocation invocation) {
        nettyClient.send(invocation);
        String requestId = invocation.getRequestId();
        DefaultFuture defaultFuture = new DefaultFuture(requestId);
        try {
            return defaultFuture.get(timeout);
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
