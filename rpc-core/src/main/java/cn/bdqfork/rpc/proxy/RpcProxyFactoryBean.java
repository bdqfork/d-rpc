package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invoker;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcProxyFactoryBean<T> implements RpcProxyFactory<T> {
    private Invoker<RpcResponse> invoker;
    private Class<?> serviceInterface;
    private String refName;

    private RpcProxyFactoryBean(Invoker<RpcResponse> invoker, Class<?> serviceInterface, String refName) {
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

    public static class Builder {
        private Invoker<RpcResponse> invoker;
        private Class<?> serviceInterface;
        private String refName;

        public Builder invoker(Invoker<RpcResponse> invoker) {
            this.invoker = invoker;
            return this;
        }

        public Builder serviceInterface(Class<?> serviceInterface) {
            this.serviceInterface = serviceInterface;
            return this;
        }

        public Builder refName(String refName) {
            this.refName = refName;
            return this;
        }

        public RpcProxyFactoryBean<?> build() {
            return new RpcProxyFactoryBean<>(invoker, serviceInterface, refName);
        }
    }


}
