package cn.bdqfork.rpc.consumer.invoker;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.consumer.context.RpcContext;
import cn.bdqfork.rpc.consumer.context.RpcContextManager;
import cn.bdqfork.rpc.consumer.exchanger.Exchanger;
import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.consumer.context.DefaultFuture;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.consumer.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
