package cn.bdqfork.rpc.cluster;

import cn.bdqfork.rpc.remote.Invoker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bdq
 * @since 2019/9/8
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers) {
        return invokers.get(count.getAndIncrement() % invokers.size());
    }

}
