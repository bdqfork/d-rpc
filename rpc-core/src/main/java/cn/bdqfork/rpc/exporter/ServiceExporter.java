package cn.bdqfork.rpc.exporter;


import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class ServiceExporter implements Exporter {
    private Set<URL> localCache = new LinkedHashSet<>();
    private Registry registry;

    public ServiceExporter(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void export(URL url) {
        localCache.add(url);
        registry.register(url);
    }

    public Set<URL> getLocalCache() {
        return localCache;
    }
}
