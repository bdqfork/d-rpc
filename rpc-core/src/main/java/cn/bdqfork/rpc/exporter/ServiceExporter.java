package cn.bdqfork.rpc.exporter;


import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.registry.URLBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class ServiceExporter implements Exporter {
    private Set<URL> localCache = new LinkedHashSet<>();
    private ProtocolConfig protocolConfig;
    private Registry registry;

    public ServiceExporter(ProtocolConfig protocolConfig, Registry registry) {
        this.protocolConfig = protocolConfig;
        this.registry = registry;
    }

    @Override
    public void export(String applicationName, String group, String serviceName, String refName) {
        URL url = URLBuilder.providerUrl(protocolConfig, serviceName)
                .applicationName(applicationName)
                .group(group)
                .refName(refName)
                .side(Const.PROVIDER_SIDE)
                .getUrl();
        localCache.add(url);
        registry.register(url);
    }

    public Set<URL> getLocalCache() {
        return localCache;
    }
}
