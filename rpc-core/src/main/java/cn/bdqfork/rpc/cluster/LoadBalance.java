package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public interface LoadBalance {
    <T> Invoker<T> select(List<Invoker<T>> invokers) throws RpcException;
}
