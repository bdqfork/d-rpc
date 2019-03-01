package cn.bdqfork.rpc.consumer.remote;

import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.consumer.config.Configration;
import cn.bdqfork.rpc.netty.client.NettyClient;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.*;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class Exchanger implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(Exchanger.class);
    private Configration configration;

    private ConcurrentHashMap<String, Map<String, NettyClient>> map = new ConcurrentHashMap<>();

    private Registry registry;

    public Exchanger(Configration configration, Registry registry) {
        this.configration = configration;
        this.registry = registry;
    }

    public void refreshRemoteServcie(URL url) {
        String serviceName = url.getServiceName();

        Set<String> remoteAddress = registry.getServiceAddress(url);

        Map<String, NettyClient> clientMap = map.get(serviceName);
        Set<String> localAddress = clientMap.keySet();
        localAddress.removeAll(remoteAddress);

        localAddress.forEach(address -> {
            NettyClient nettyClient = clientMap.get(address);
            nettyClient.close();
            clientMap.remove(address);
        });

        remoteAddress.stream()
                .filter(address -> !clientMap.containsKey(address))
                .forEach(address -> {
                    String[] hostPort = address.split(":");
                    NettyClient nettyClient = new NettyClient(hostPort[0], Integer.parseInt(hostPort[1]));
                    nettyClient.open();
                    clientMap.put(address, nettyClient);
                });
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
        map.putIfAbsent(serviceName, new ConcurrentHashMap<>(8));
        refreshRemoteServcie(url);
    }

    public List<NettyClient> getNettyClients(String group, String serviceName) throws RpcException {
        Map<String, String> paramterMap = new HashMap<>(8);
        paramterMap.put(Const.GROUP_KEY, group);
        paramterMap.put(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        URL url = new URL("consumer", configration.getHost(), configration.getPort(), serviceName, paramterMap);
        while (true) {
            List<NettyClient> nettyClients = new LinkedList<>(map.get(serviceName).values());
            if (nettyClients.size() != 0) {
                return nettyClients;
            } else {
                delay();
                refreshRemoteServcie(url);
                throw new RpcException("No provider available from registry");
            }
        }
    }

    public void removeNettyClient(String service, NettyClient nettyClient) {
        map.get(service).remove(nettyClient.getHost() + ":" + nettyClient.getPort());
    }

    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void notify(URL url, RegistryEvent event) {
        ZkRegistryEvent zkRegistryEvent = (ZkRegistryEvent) event;
        if ("NodeChildrenChanged".equals(zkRegistryEvent.getEvent())) {
            refreshRemoteServcie(url);
            registry.subscribe(url, this);
        }
    }

}
