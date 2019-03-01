package cn.bdqfork.rpc.consumer.exchanger;

import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.consumer.config.Configration;
import cn.bdqfork.rpc.consumer.client.NettyClient;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.*;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.acl.Group;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class Exchanger implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(Exchanger.class);
    private Configration configration;

    private ConcurrentHashMap<String, ClientPool> map = new ConcurrentHashMap<>();

    private Registry registry;

    public Exchanger(Configration configration, Registry registry) {
        this.configration = configration;
        this.registry = registry;
    }

    public void register(String group, String serviceName) {
        Map<String, String> paramterMap = new HashMap<>(8);
        paramterMap.put(Const.GROUP_KEY, group);
        paramterMap.put(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        URL url = new URL("consumer", configration.getHost(), configration.getPort(), serviceName, paramterMap);
        registry.register(url);
    }

    public void subscribe(String group, String serviceName) {
        Map<String, String> paramterMap = new HashMap<>(8);
        paramterMap.put(Const.GROUP_KEY, group);
        paramterMap.put(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        URL url = new URL("consumer", configration.getHost(), configration.getPort(), serviceName, paramterMap);
        registry.subscribe(url, this);
        map.putIfAbsent(getKey(url), new ClientPool(() -> refreshRemoteServcie(url)));
        refreshRemoteServcie(url);
    }

    public ClientPool getClientPool(String group, String serviceName) {
        Map<String, String> paramterMap = new HashMap<>(8);
        paramterMap.put(Const.GROUP_KEY, group);
        paramterMap.put(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        URL url = new URL("consumer", configration.getHost(), configration.getPort(), serviceName, paramterMap);
        return map.get(getKey(url));
    }

    @Override
    public void notify(URL url, RegistryEvent event) {
        ZkRegistryEvent zkRegistryEvent = (ZkRegistryEvent) event;
        if ("NodeChildrenChanged".equals(zkRegistryEvent.getEvent())) {
            refreshRemoteServcie(url);
            registry.subscribe(url, this);
        }
    }

    private void refreshRemoteServcie(URL url) {
        Set<String> remoteAddress = registry.getServiceAddress(url);
        ClientPool clientPool = map.get(getKey(url));
        clientPool.refresh(remoteAddress);
    }

    private String getKey(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, Const.DEFAULT_GROUP);
        return "/" + group + url.toServiceCategory();
    }

}
