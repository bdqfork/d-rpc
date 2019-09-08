package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcInvoker<T> extends AbstractInvoker<T> {
    private static final Logger log = LoggerFactory.getLogger(RpcInvoker.class);
    private RemoteClient[] remoteClients;
    private long timeout;
    private int retries;
    private int count = 0;

    public RpcInvoker(Class<T> type, URL url) {
        super(null, type, url);
        init();
    }

    private void init() {
        timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY, "1000"));
        retries = Integer.parseInt(url.getParameter(Const.RETRY_KEY, "3"));
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws RpcException {
        //超时重试
        RemoteClient client = remoteClients[count++ % remoteClients.length];
        try {
            return (Result) client.send(invocation, timeout).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (RemoteClient client : remoteClients) {
            if (client.isRunning()) {
                client.close();
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

    @Override
    public URL getUrl() {
        return url;
    }

    public void setRemoteClients(RemoteClient[] remoteClients) {
        this.remoteClients = remoteClients;
    }

}
