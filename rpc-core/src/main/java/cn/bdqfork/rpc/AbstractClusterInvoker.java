package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcResponse;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public abstract class AbstractClusterInvoker<T> implements Invoker<T> {
    private Directory<T> directory;

    public AbstractClusterInvoker(Directory<T> directory) {
        this.directory = directory;
    }

    @Override
    public RpcResponse invoke(Invocation invocation) throws RpcException {
        List<Invoker<T>> invokers = directory.list();
        return doInvoke(invocation, invokers, null);
    }

    protected abstract RpcResponse doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException;

    @Override
    public Class<T> getInterface() {
        return directory.getServiceInterface();
    }

    @Override
    public URL getUrl() {
        return directory.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return directory.isAvailable();
    }

}
