package cn.bdqfork.rpc.consumer.proxy;

import cn.bdqfork.rpc.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class DefaultProxyFactory implements ProxyFactory {

    @Override
    public <T> T getRemoteProxyInstance(Invoker<Object> invoker, Class<T> serviceInterface, String refName) {
        return (T) new JdkInvocationHandler(invoker, serviceInterface, refName).newProxyInstance();
    }
}
