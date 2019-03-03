package cn.bdqfork.rpc.proxy;


import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @date 2019-02-15
 */
public interface RpcProxyFactory<T> {
    /**
     * 获取远程代理
     *
     * @param proxyType
     * @return
     * @throws RpcException
     */
     T getProxy(ProxyType proxyType) throws RpcException;
}
