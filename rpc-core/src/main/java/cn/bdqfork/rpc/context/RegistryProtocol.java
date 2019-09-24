package cn.bdqfork.rpc.context;

import cn.bdqfork.common.Node;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.*;
import cn.bdqfork.rpc.cluster.Cluster;
import cn.bdqfork.rpc.cluster.ClusterDirectory;
import cn.bdqfork.rpc.context.filter.Filter;
import cn.bdqfork.rpc.context.remote.RpcServer;
import cn.bdqfork.rpc.context.remote.RpcServerFactory;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-08-24
 */
public class RegistryProtocol implements Protocol {
    private static final Map<String, RpcServer> rpcServerMap = new ConcurrentHashMap<>();
    private RpcServerFactory rpcServerFactory = ExtensionLoader.getExtensionLoader(RpcServerFactory.class)
            .getAdaptiveExtension();
    private ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class)
            .getAdaptiveExtension();
    private Cluster cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getAdaptiveExtension();
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
            String key = url.getAddress();
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
        Invoker<T> invoker = proxyFactory.getInvoker(proxy, type, url);
        return buildInvokerChain(invoker, Const.PROTOCOL_PROVIDER);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        ClusterDirectory<T> directory = new ClusterDirectory<>(type, url);
        directory.setRegistries(registries);
        directory.subscribe();
        Invoker<T> invoker = cluster.join(directory);
        return buildInvokerChain(invoker, Const.PROTOCOL_CONSUMER);
    }

    private <T> Invoker<T> buildInvokerChain(Invoker<T> invoker, String group) {
        Invoker<T> last = invoker;
        List<Filter> filters = ExtensionLoader.getExtensionLoader(Filter.class)
                .getActivateExtensions(invoker.getUrl(), group);
        for (int i = 0; i < filters.size(); i++) {
            Filter next = filters.get(i);
            Invoker<T> finalLast = last;
            last = new Invoker<T>() {
                @Override
                public Class<T> getInterface() {
                    return finalLast.getInterface();
                }

                @Override
                public Result invoke(Invocation invocation) throws RpcException {
                    return next.invoke(finalLast, invocation);
                }

                @Override
                public URL getUrl() {
                    return finalLast.getUrl();
                }

                @Override
                public boolean isAvailable() {
                    return finalLast.isAvailable();
                }

                @Override
                public void destroy() {
                    finalLast.destroy();
                }
            };
        }
        return last;
    }

    public static void destroy() {
        rpcServerMap.values().forEach(Node::destroy);
    }

}
