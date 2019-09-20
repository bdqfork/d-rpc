package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Directory;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class AvailableClusterInvoker<T> extends AbstractClusterInvoker<T> {
    public AvailableClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadBalance) throws RpcException {
        for (Invoker<T> invoker : invokers) {
            if (invoker.isAvailable()) {
                return invoker.invoke(invocation);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RpcException("No provider available in " + invokers);
    }


}
