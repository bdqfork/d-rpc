package cn.bdqfork.rpc.consumer;

import cn.bdqfork.rpc.common.exception.RpcException;
import cn.bdqfork.rpc.common.exception.TimeoutException;
import cn.bdqfork.rpc.protocol.client.ClientPool;
import cn.bdqfork.rpc.protocol.client.NettyClient;
import cn.bdqfork.rpc.consumer.context.DefaultFuture;
import cn.bdqfork.rpc.consumer.context.RpcContext;
import cn.bdqfork.rpc.consumer.context.RpcContextManager;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class RpcInvoker implements Invoker<Object> {
    private static final Logger log = LoggerFactory.getLogger(RpcInvoker.class);
    private ClientPool clientPool;
    private long timeout;
    private int retryTime;

    public RpcInvoker(ClientPool clientPool, long timeout, int retryTime) {
        this.clientPool = clientPool;
        this.timeout = timeout;
        this.retryTime = retryTime;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {

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

                if (retryCount > retryTime) {
                    RpcContextManager.removeContext(rpcContext.getRequestId());
                    throw e;
                }
            }
        }
    }

    private int retry(int retryCount) {

        retryCount++;
        int delayTime = 1000 * retryCount;

        if (retryCount <= retryTime) {
            log.warn("failed to invoke method , will retry after {} second !", delayTime);
        }
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return retryCount;
    }

}
