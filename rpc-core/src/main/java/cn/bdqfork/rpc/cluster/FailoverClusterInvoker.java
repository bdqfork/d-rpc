package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Directory;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @since 2019/9/8
 */
public class FailoverClusterInvoker<T> extends AbstractClusterInvoker<T> {
    private static final Logger log = LoggerFactory.getLogger(FailoverClusterInvoker.class);
    private static final int retryTime = 1000;

    public FailoverClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        List<Invoker<T>> copyInvokers = new ArrayList<>(invokers);
        int reties = Integer.parseInt(this.getUrl().getParameter(Const.RETRY_KEY));
        int count = 0;
        RpcException lastException = null;
        while (count++ < reties) {
            try {
                Invoker<T> invoker = loadBalance.select(copyInvokers);
                return invoker.invoke(invocation);
            } catch (RpcException e) {
                if (count > 1) {
                    log.warn("Retry to invoke {} method in {}st time, will retry in {} millisecond! ",
                            invocation.getMethodName(), count, retryTime);
                } else {
                    log.warn("Failed to invoke {} method in {}st time, will retry in {} millisecond! ",
                            invocation.getMethodName(), count, retryTime);
                }
                try {
                    Thread.sleep(retryTime);
                } catch (InterruptedException ex) {
                    throw new RpcException(ex);
                }
                copyInvokers = new ArrayList<>(this.list(invocation));
                lastException = e;
            }
        }
        throw lastException != null ? lastException :
                new RpcException(String.format("Although retry to invoke %s method %d times, it's failed finally! ",
                        invocation.getMethodName(), count));
    }
}
