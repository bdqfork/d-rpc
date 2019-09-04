package cn.bdqfork.rpc.remote.context;

import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @since 2019-03-01
 */
public interface RpcFuture<T> {
    /**
     * 是否已完成
     *
     * @return
     */
    boolean isDone();

    /**
     * 获取结果
     *
     * @return
     * @throws RpcException
     */
    T get() throws RpcException;

    /**
     * 设置结果
     *
     * @param t
     */
    void setResult(T t);

    void cancle();
}
