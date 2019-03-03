package cn.bdqfork.rpc.consumer.proxy;

import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @date 2019-03-02
 */
public interface ProxyInstanceGenerator<T> {
    /**
     * 创建代理实例
     *
     * @return
     */
    T newProxyInstance() throws RpcException;
}
