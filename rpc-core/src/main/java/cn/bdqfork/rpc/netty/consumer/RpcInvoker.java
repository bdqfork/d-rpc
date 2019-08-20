package cn.bdqfork.rpc.netty.consumer;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.netty.client.ClientPool;
import cn.bdqfork.rpc.netty.client.NettyClient;
import cn.bdqfork.rpc.netty.consumer.context.DefaultFuture;
import cn.bdqfork.rpc.netty.consumer.context.RpcContext;
import cn.bdqfork.rpc.netty.consumer.context.RpcContextManager;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcInvoker implements Invoker<RpcResponse> {
    private static final Logger log = LoggerFactory.getLogger(RpcInvoker.class);
    private ClientPool clientPool;
    private long timeout;
    private int retries;

    public RpcInvoker(long timeout, int retries) {
        this.timeout = timeout;
        this.retries = retries;
    }

    @Override
    public RpcResponse invoke(Invocation invocation) throws RpcException {

        DefaultFuture<RpcResponse> defaultFuture = new DefaultFuture<>();
        RpcContext rpcContext = new RpcContext(invocation.getRequestId(), defaultFuture);
        RpcContextManager.registerContext(rpcContext);

        int retryCount = 0;
        while (true) {

            NettyClient client = clientPool.getNettyClient();

            try {
                client.send(invocation);
            } catch (RpcException e) {
                clientPool.removeClient(client);
            }

            try {
                return defaultFuture.get(timeout);
            } catch (TimeoutException e) {
                retryCount = retry(retryCount);

                if (retryCount > retries) {
                    RpcContextManager.removeContext(rpcContext.getRequestId());
                    throw e;
                }
            }
        }
    }

    private int retry(int retryCount) {

        retryCount++;
        int delayTime = 1000 * retryCount;

        if (retryCount <= retries) {
            log.warn("failed to invoke method , will retry after {} second !", delayTime);
        }
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return retryCount;
    }

    public void setClientPool(ClientPool clientPool) {
        this.clientPool = clientPool;
    }
}
