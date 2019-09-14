package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.cluster.Cluster;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.exporter.RpcExporter;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Directory;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.RpcServerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-08-24
 */
public class RegistryProtocol implements Protocol {
    private RpcServerFactory rpcServerFactory = ExtensionLoader.getExtension(RpcServerFactory.class);
    private ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class);
    private Cluster cluster = ExtensionLoader.getExtension(Cluster.class);
    private static final Map<String, RpcServer> rpcServerMap = new ConcurrentHashMap<>();
    private List<Registry> registries;

    public RegistryProtocol(List<Registry> registries) {
        this.registries = registries;
    }

    @Override
    public <T> Exporter export(Invoker<T> invoker) {
        RpcExporter rpcExporter = new RpcExporter(invoker);
        rpcExporter.setRegistries(registries);
        URL url = invoker.getUrl();
        String protocol = url.getProtocol();
        if (Const.PROTOCOL_PROVIDER.equals(protocol)) {
            String key = url.getHost() + url.getPort();
            buildServerUrl(url);
            RpcServer rpcServer = rpcServerMap.get(key);
            if (rpcServer == null) {

                rpcServer = rpcServerFactory.getServer(buildServerUrl(url));

                rpcServer.start();

                rpcServerMap.put(key, rpcServer);
            }

            rpcServer.addInvoker(invoker);
        }
        return rpcExporter;
    }

    private URL buildServerUrl(URL url) {
        String server = url.getParameter(Const.SERVER_KEY);
        String serialization = url.getParameter(Const.SERIALIZATION_KEY);
        URL serverUrl = new URL(server, url.getHost(), url.getPort(), "");
        serverUrl.addParameter(Const.SERIALIZATION_KEY, serialization);
        return serverUrl;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return proxyFactory.getInvoker(proxy, type, url);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        Directory<T> directory = new Directory<>(type, url);
        directory.setRegistries(registries);
        directory.subscribe();
        return cluster.join(directory);
    }

}
