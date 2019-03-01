package cn.bdqfork.rpc.consumer.context;

import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @date 2019-03-01
 */
public interface RpcFuture<T> {
    boolean isDone();

    T get(long timeout) throws RpcException;

    void setResult(T t);
}
