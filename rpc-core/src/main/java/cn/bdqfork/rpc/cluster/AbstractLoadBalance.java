package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.util.CollectionUtils;
import cn.bdqfork.rpc.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019/9/11
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers) throws RpcException {
        if (invokers == null || CollectionUtils.isEmpty(invokers)) {
            throw new RpcException("No providers");
        }
        return doSelect(invokers);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers);
}
