package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.exporter.RpcExporter;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-08-24
 */
public class RegistryProtocol {
    private ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class);
    private Cluster cluster = ExtensionLoader.getExtension(Cluster.class);
    private Registry registry;

    public RegistryProtocol(Registry registry) {
        this.registry = registry;
    }

    public <T> Exporter export(Invoker<T> invoker) {
        RpcExporter rpcExporter = new RpcExporter(invoker);
        rpcExporter.setRegistry(registry);
        return rpcExporter;
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return proxyFactory.getInvoker(proxy, type, url);
    }

    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        RegistryDirectory<T> registryDirectory = new RegistryDirectory<>(type, url);
        registryDirectory.setRegistry(registry);
        registryDirectory.subscribe();
        return cluster.join(registryDirectory);
    }

}
