package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {
    @Adaptive(Const.LOADBALANCE_KEY)
    <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;
}
