package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.Result;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class AvailableClusterInvoker<T> extends AbstractClusterInvoker<T> {
    public AvailableClusterInvoker(RegistryDirectory<T> registryDirectory) {
        super(registryDirectory);
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
