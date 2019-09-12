package cn.bdqfork.rpc;

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
    private RemoteClient[] remoteClients;
    private AtomicInteger count = new AtomicInteger(0);

    public RpcInvoker(Class<T> type, URL url) {
        super(null, type, url);
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws RpcException {
        RemoteClient client = remoteClients[count.getAndIncrement() % remoteClients.length];
        try {
            return (Result) client.send(invocation).get();
        } catch (InterruptedException | ExecutionException | RpcException e) {
            throw new RpcException("Invoke remote method error !", e.getCause());
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
