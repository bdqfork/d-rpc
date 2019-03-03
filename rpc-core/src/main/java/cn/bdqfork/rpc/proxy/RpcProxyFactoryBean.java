package cn.bdqfork.rpc.proxy;

import cn.bdqfork.rpc.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class RpcProxyFactoryBean<T> implements RpcProxyFactory<T> {
    private Invoker<Object> invoker;
    private Class<T> serviceInterface;
    private String refName;

    private RpcProxyFactoryBean(Invoker<Object> invoker, Class<T> serviceInterface, String refName) {
        this.invoker = invoker;
        this.serviceInterface = serviceInterface;
        this.refName = refName;
    }

    @Override
    public T getProxy(ProxyType proxyType) throws RpcException {
        if (proxyType == ProxyType.JDK) {
            return new JdkProxyInstanceGenerator<T>(invoker, serviceInterface, refName).newProxyInstance();
        } else {
            return new JavassistProxyInstanceGenerator<T>(invoker, serviceInterface, refName).newProxyInstance();
        }
    }

    public static class Builder<T> {
        private Invoker<Object> invoker;
        private Class<T> serviceInterface;
        private String refName;

        public Builder<T> invoker(Invoker<Object> invoker) {
            this.invoker = invoker;
            return this;
        }

        public Builder<T> serviceInterface(Class<T> serviceInterface) {
            this.serviceInterface = serviceInterface;
            return this;
        }

        public Builder<T> refName(String refName) {
            this.refName = refName;
            return this;
        }

        public RpcProxyFactoryBean<T> build() {
            return new RpcProxyFactoryBean<>(invoker, serviceInterface, refName);
        }
    }


}
