package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.util.CollectionUtils;
import cn.bdqfork.rpc.remote.Invoker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bdq
 * @since 2019/9/8
 */
public class RoundRobinLoadBalance implements LoadBalance {
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            throw new RpcException("no providers");
        }
        return invokers.get(count.getAndIncrement() % invokers.size());
    }

}
