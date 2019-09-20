package cn.bdqfork.rpc.cluster;

import cn.bdqfork.rpc.Invoker;

import java.util.List;
import java.util.Random;

/**
 * @author bdq
 * @since 2019/9/11
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers) {
        int size = invokers.size();
        Random random = new Random();
        int index = random.nextInt(size);
        return invokers.get(index);
    }
}
