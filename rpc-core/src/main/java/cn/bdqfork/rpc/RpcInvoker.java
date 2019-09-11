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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcInvoker<T> extends AbstractInvoker<T> {
    private static final Logger log = LoggerFactory.getLogger(RpcInvoker.class);
    private RemoteClient[] remoteClients;
    private long timeout;
    private AtomicInteger count = new AtomicInteger(0);

    public RpcInvoker(Class<T> type, URL url) {
        super(null, type, url);
        init();
    }

    private void init() {
        timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY, "1000"));
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws RpcException {
        RemoteClient client = remoteClients[count.getAndIncrement() % remoteClients.length];
        try {
            return (Result) client.send(invocation).get();
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

    @Override
    public URL getUrl() {
        return url;
    }

    public void setRemoteClients(RemoteClient[] remoteClients) {
        this.remoteClients = remoteClients;
    }

}
