package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-08-26
 */
public interface ProxyFactory {
    <T> T getProxy(Invoker invoker) throws RpcException;

    <T> Invoker getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
}
