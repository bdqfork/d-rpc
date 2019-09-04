package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Result;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public abstract class AbstractClusterInvoker<T> implements Invoker<T> {
    private RegistryDirectory<T> registryDirectory;

    public AbstractClusterInvoker(RegistryDirectory<T> registryDirectory) {
        this.registryDirectory = registryDirectory;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        List<Invoker<T>> invokers = registryDirectory.list(invocation);
        return doInvoke(invocation, invokers, null);
    }

    protected abstract Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException;

    @Override
    public Class<T> getInterface() {
        return registryDirectory.getServiceInterface();
    }

    @Override
    public URL getUrl() {
        return registryDirectory.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return registryDirectory.isAvailable();
    }

    @Override
    public void destroy() {
        registryDirectory.destroy();
    }
}
