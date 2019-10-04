package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.Directory;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;

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
    public Result invoke(Invocation invocation) throws RpcException {
        List<Invoker<T>> invokers = directory.list(invocation);
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getAdaptiveExtension();
        return doInvoke(invocation, invokers, loadBalance);
    }

    protected abstract Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException;

    protected List<Invoker<T>> list(Invocation invocation) {
        return directory.list(invocation);
    }

    @Override
    public Class<T> getInterface() {
        return directory.getInterface();
    }

    @Override
    public URL getUrl() {
        return directory.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return directory.isAvailable();
    }

    @Override
    public void destroy() {
        directory.destroy();
    }
}
