package cn.bdqfork.protocol;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.rpc.context.AsyncResult;

import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2019/9/26
 */
public class AsyncToSyncInvoker<T> implements Invoker<T> {
    private Invoker<T> invoker;

    public AsyncToSyncInvoker(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        AsyncResult asyncResult = (AsyncResult) invoker.invoke(invocation);
        boolean async = getUrl().getParameter(Const.ASYNC_KEY);
        if (async) {
            return asyncResult;
        }
        try {
            return asyncResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RpcException(e.getCause());
        }
    }

    @Override
    public URL getUrl() {
        return invoker.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    @Override
    public void destroy() {
        invoker.destroy();
    }
}
