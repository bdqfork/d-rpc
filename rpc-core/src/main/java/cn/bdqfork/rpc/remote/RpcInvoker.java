package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.ConnectionLostException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import cn.bdqfork.rpc.remote.context.RpcContext;
import cn.bdqfork.rpc.remote.invoker.Invoker;
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

    @Override
    public RpcResponse invoke(RpcContext.Context context) throws RpcException {
        DefaultFuture<RpcResponse> defaultFuture = new DefaultFuture<>();
        RpcContext.registerContext(context.getRequestId(), defaultFuture);

        int retryCount = 0;
        while (true) {
            RemoteClient client = null;
            try {
                client = clientPool.getRemoteClient();
                client.send(context);
            } catch (ConnectionLostException e) {
                log.warn(e.getMessage());
                clientPool.removeClient(client);
            } catch (RpcException e) {
                log.warn(e.getMessage());
            }

            try {
                return defaultFuture.get(timeout);
            } catch (TimeoutException e) {
                retryCount = retry(retryCount);

                if (retryCount > retries) {
                    RpcContext.removeContext(context.getRequestId());
                    throw e;
                }
            }
        }
    }
}
