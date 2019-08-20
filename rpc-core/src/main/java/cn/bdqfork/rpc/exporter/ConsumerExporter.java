package cn.bdqfork.rpc.exporter;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.netty.NettyInitializer;
import cn.bdqfork.rpc.netty.client.ClientPool;
import cn.bdqfork.rpc.netty.consumer.ClientContextHandler;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.protocol.serializer.Serializer;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.registry.URLBuilder;
import cn.bdqfork.rpc.registry.event.NodeEvent;
import cn.bdqfork.rpc.registry.event.RegistryEvent;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class ConsumerExporter implements Exporter, Notifier {
    private Set<URL> localCache = new LinkedHashSet<>();

    private ProtocolConfig protocolConfig;

    private Map<String, ClientPool> map = new ConcurrentHashMap<>();

    private Registry registry;

    private Serializer serializer;

    public ConsumerExporter(ProtocolConfig protocolConfig, Registry registry) {
        this(protocolConfig, registry, new HessianSerializer());
    }

    public ConsumerExporter(ProtocolConfig protocolConfig, Registry registry, Serializer serializer) {
        this.protocolConfig = protocolConfig;
        this.registry = registry;
        this.serializer = serializer;
    }

    @Override
    public void export(String applicationName, String group, String serviceName, String refName) {
        URL url = bulidUrl(applicationName, group, serviceName, refName);
        register(url);
        subscribe(url);
    }

    private URL bulidUrl(String applicationName, String group, String serviceName, String refName) {
        return URLBuilder.consumerUrl(protocolConfig, serviceName)
                    .applicationName(applicationName)
                    .group(group)
                    .refName(refName)
                    .getUrl();
    }

    public void register(URL url) {
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        localCache.add(url);
        registry.register(url);
    }

    public void subscribe(URL url) {
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        registry.subscribe(url, this);

        ClientContextHandler clientContextHandler = new ClientContextHandler();
        NettyInitializer.ChannelHandlerElement channelHandlerElement = new NettyInitializer.ChannelHandlerElement(clientContextHandler);
        NettyInitializer nettyInitializer = new NettyInitializer(serializer, channelHandlerElement);

        map.putIfAbsent(getKey(url), new ClientPool(nettyInitializer, () -> refreshRemoteServices(url)));

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