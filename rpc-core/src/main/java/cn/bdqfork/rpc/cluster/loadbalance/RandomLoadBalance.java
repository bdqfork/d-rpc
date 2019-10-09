package cn.bdqfork.rpc.cluster.loadbalance;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;

import java.util.List;
import java.util.Random;

/**
 * @author bdq
 * @since 2019/9/11
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "random";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int size = invokers.size();
        Random random = new Random();
        int index = random.nextInt(size);
        return invokers.get(index);
    }
}
