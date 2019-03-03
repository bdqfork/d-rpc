package cn.bdqfork.rpc.consumer.client;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.consumer.exchanger.RefreshCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class ClientPool {
    private Map<String, NettyClient> clientMap = new ConcurrentHashMap<>();
    private RefreshCallback callback;
    private int i;

    public ClientPool(RefreshCallback callback) {
        this.callback = callback;
    }

    public void refresh(Set<String> remoteAddress) {
        Set<String> localAddress = clientMap.keySet();
        //求差集
        localAddress.removeAll(remoteAddress);

        //移除过期的连接
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

    public NettyClient getNettyClient() throws RpcException {
        while (true) {
            List<NettyClient> nettyClients = new ArrayList<>(clientMap.values());
            if (nettyClients.size() != 0) {
                if (i == Integer.MAX_VALUE) {
                    i = 0;
                }
                //负载均衡
                return nettyClients.get(i++ % nettyClients.size());
            } else {
                callback.refresh();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new RpcException("No provider available from registry");
            }
        }
    }

    public void removeClient(NettyClient nettyClient) {
        clientMap.remove(nettyClient.getHost() + ":" + nettyClient.getPort());
    }

}
