package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-08-26
 */
public interface ProxyFactory {
    <T> T getProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url);
}
