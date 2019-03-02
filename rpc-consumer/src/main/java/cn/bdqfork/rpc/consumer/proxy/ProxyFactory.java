package cn.bdqfork.rpc.consumer.proxy;


import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @date 2019-02-15
 */
public interface ProxyFactory<T> {
    /**
     * 获取远程代理
     *
     * @param proxyType
     * @return
     * @throws RpcException
     */
     T getProxy(ProxyType proxyType) throws RpcException;
}
