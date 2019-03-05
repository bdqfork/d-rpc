package cn.bdqfork.rpc.exporter;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.registry.URLBuilder;
import cn.bdqfork.rpc.registry.event.NodeEvent;
import cn.bdqfork.rpc.registry.event.RegistryEvent;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class Exchanger implements Exporter, Notifier {
    private Set<URL> localCache = new LinkedHashSet<>();

    private ProtocolConfig protocolConfig;

    private ConcurrentHashMap<String, ClientPool> map = new ConcurrentHashMap<>();

    private Registry registry;

    public Exchanger(ProtocolConfig protocolConfig, Registry registry) {
        this.protocolConfig = protocolConfig;
        this.registry = registry;
    }

    @Override
    public void export(String applicationName, String group, String serviceName, String refName) {
        URL url = URLBuilder.consumerUrl(protocolConfig, serviceName)
                .applicationName(applicationName)
                .group(group)
                .refName(refName)
                .getUrl();
        register(url);
        subscribe(url);
    }

    public void register(URL url) {
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        localCache.add(url);
        registry.register(url);
    }

    public void subscribe(URL url) {
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        registry.subscribe(url, this);
        map.putIfAbsent(getKey(url), new ClientPool(() -> refreshRemoteServices(url)));
        refreshRemoteServices(url);
    }

    public ClientPool getClientPool(String group, String serviceName) {
        String key = "/" + group + "/" + serviceName;
        return map.get(key);
    }

    @Override
    public void notify(URL url, RegistryEvent event) {
        if (NodeEvent.CHANGED == event.getEvent()) {
            refreshRemoteServices(url);
            registry.subscribe(url, this);
        }
    }

    public Set<URL> getLocalCache() {
        return localCache;
    }

    private void refreshRemoteServices(URL url) {
        Set<String> remoteAddress = registry.getServiceAddress(url);
        ClientPool clientPool = map.get(getKey(url));
        clientPool.refresh(remoteAddress);
    }

    private String getKey(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, Const.DEFAULT_GROUP);
        return "/" + group + url.toServiceCategory();
    }

}
