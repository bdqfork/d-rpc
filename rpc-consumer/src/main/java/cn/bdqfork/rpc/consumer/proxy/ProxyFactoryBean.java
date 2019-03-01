package cn.bdqfork.rpc.consumer.proxy;

import cn.bdqfork.rpc.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class ProxyFactoryBean implements ProxyFactory {
    @Override
    public <T> T getJdkProxy(Invoker<Object> invoker, Class<T> serviceInterface, String refName) {
        return (T) new JdkInvocationHandler(invoker, serviceInterface, refName).newProxyInstance();
    }
}
