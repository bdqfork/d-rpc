package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.*;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.*;
import cn.bdqfork.rpc.cluster.Cluster;
import cn.bdqfork.rpc.filter.Filter;
import cn.bdqfork.rpc.protocol.Protocol;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bdq
 * @since 2019-08-24
 */
public class RegistryProtocol implements Protocol {
    private static final Map<String, Registry> REGISTRIES = new ConcurrentHashMap<>();
    private List<Exporter> exporters = new CopyOnWriteArrayList<>();
    private Cluster cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getAdaptiveExtension();
    private Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
    private RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();

    @Override
    public <T> Exporter export(Invoker<T> invoker) {
        invoker = buildInvokerChain(invoker, Const.PROVIDER_SIDE);

        URL url = invoker.getUrl();
        String server = url.getParameter(Const.SERVER_KEY);
        url.setProtocol(server);

        List<Registry> registries = getRegistries(url);

        url.removeParameter(Const.REGISTRY_KEY);
        registries.forEach(registry -> registry.register(url));

        Exporter exporter = new DestroyableExporter(protocol.export(invoker), registries);
        exporters.add(exporter);
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        List<Registry> registries = getRegistries(url);
        RegistryDirectory<T> directory = new RegistryDirectory<>(type, url, registries);
        directory.subscribe();
        Invoker<T> invoker = cluster.join(directory);
        return buildInvokerChain(invoker, Const.CONSUMER_SIDE);
    }

    private List<Registry> getRegistries(URL url) {
        String registryUrlString = url.getParameter(Const.REGISTRY_KEY);
        String[] registryUrlStrings = registryUrlString.split(",");
        List<Registry> registries = new ArrayList<>(registryUrlStrings.length);
        for (String urlString : registryUrlStrings) {
            registries.add(getRegistry(urlString));
        }
        return registries;
    }

    private Registry getRegistry(String urlString) {
        Registry registry = REGISTRIES.get(urlString);
        if (registry == null) {
            registry = registryFactory.getRegistry(new URL(urlString));
            REGISTRIES.putIfAbsent(urlString, registry);
        }
        return registry;
    }

    @Override
    public void destory() {
        exporters.forEach(Exporter::undoExport);
        REGISTRIES.values().forEach(Node::destroy);
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

    private static class DestroyableExporter implements Exporter {
        private Exporter exporter;
        private List<Registry> registries;

        public DestroyableExporter(Exporter exporter, List<Registry> registries) {
            this.exporter = exporter;
            this.registries = registries;
        }

        @Override
        public void undoExport() {
            URL url = getInvoker().getUrl();
            registries.forEach(registry -> registry.undoRegister(url));
            exporter.undoExport();
        }

        @Override
        public Invoker getInvoker() {
            return exporter.getInvoker();
        }
    }

}
