package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.util.CollectionUtils;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019/9/11
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            throw new RpcException("No providers");
        }
        return doSelect(invokers, url, invocation);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation);
}
