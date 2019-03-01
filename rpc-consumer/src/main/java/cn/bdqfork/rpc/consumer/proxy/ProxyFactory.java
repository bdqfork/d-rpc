package cn.bdqfork.rpc.consumer.proxy;


import cn.bdqfork.rpc.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-02-15
 */
public interface ProxyFactory {
    /**
     * 获取远程代理
     *
     * @param invoker
     * @param serviceInterface
     * @param refName
     * @param <T>
     * @return
     */
    <T> T getRemoteProxyInstance(Invoker<Object> invoker, Class<T> serviceInterface, String refName);
}
