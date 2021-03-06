package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.rpc.context.AbstractInvoker;
import cn.bdqfork.rpc.context.AsyncResult;
import cn.bdqfork.rpc.context.FutureAdapter;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.Result;
import cn.bdqfork.rpc.context.DefaultFuture;
import cn.bdqfork.rpc.context.RpcContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcInvoker<T> extends AbstractInvoker<T> {
    private RemoteClient[] remoteClients;
    private AtomicInteger count = new AtomicInteger(0);

    public RpcInvoker(Class<T> type, URL url, RemoteClient[] remoteClients) {
        super(null, type, url);
        this.remoteClients = remoteClients;
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws RpcException {
        RemoteClient client = remoteClients[count.getAndIncrement() % remoteClients.length];
        try {
            DefaultFuture future = client.send(invocation);
            boolean async = url.getParameter(Const.ASYNC_KEY);
            AsyncResult asyncResult = new AsyncResult();
            asyncResult.subcribeTo(future);
            if (async) {
                RpcContext.getRpcContext()
                        .setFuture(new FutureAdapter(asyncResult));
                return AsyncResult.newDefaultAsyncResult();
            } else {
                RpcContext.getRpcContext()
                        .setFuture(null);
                return asyncResult;
            }
        } catch (RpcException e) {
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
