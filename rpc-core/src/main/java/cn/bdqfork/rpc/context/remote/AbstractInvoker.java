package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2019-09-04
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {
    private boolean isAvailable = true;
    private AtomicBoolean destroyed = new AtomicBoolean(false);
    protected T proxy;
    protected Class<T> type;
    protected URL url;

    public AbstractInvoker(T proxy, Class<T> type, URL url) {
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        return doInvoke(invocation);
    }

    protected abstract Result doInvoke(Invocation invocation) throws RpcException;

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            isAvailable = false;
        }
    }

}
