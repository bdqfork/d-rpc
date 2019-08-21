package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionUtils;
import cn.bdqfork.rpc.exporter.RefreshCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class ClientPool {
    private static final String ADDRESS_SEPARATOR = ":";
    private RemoteClientFactory remoteClientFactory = ExtensionUtils.getExtension(RemoteClientFactory.class);
    private Map<String, RemoteClient> clientMap = new ConcurrentHashMap<>();
    private String server;
    private String serialization;
    private RefreshCallback callback;
    private int i;

    public ClientPool(String server, String serialization, RefreshCallback callback) {
        this.server = server;
        this.serialization = serialization;
        this.callback = callback;
    }

    public void refresh(Set<String> remoteAddress) {
        Set<String> localAddress = clientMap.keySet();
        //求差集
        localAddress.removeAll(remoteAddress);

        //移除过期的连接
        localAddress.forEach(address -> {
            RemoteClient remoteClient = clientMap.get(address);
            remoteClient.close();
            clientMap.remove(address);
        });

        remoteAddress.stream()
                .filter(address -> !clientMap.containsKey(address))
                .forEach(address -> {
                    String[] hostPort = address.split(ADDRESS_SEPARATOR);
                    RemoteClient remoteClient = remoteClientFactory.createRemoteClient(server,serialization,
                            hostPort[0], Integer.parseInt(hostPort[1]));
                    clientMap.put(address, remoteClient);
                });
    }

    public RemoteClient getRemoteClient() throws RpcException {
        List<RemoteClient> remoteClients = new ArrayList<>(clientMap.values());
        if (remoteClients.size() != 0) {
            if (i == Integer.MAX_VALUE) {
                i = 0;
            }
            //负载均衡
            return remoteClients.get(i++ % remoteClients.size());
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

    public void removeClient(RemoteClient remoteClient) {
        clientMap.remove(remoteClient.getHost() + ADDRESS_SEPARATOR + remoteClient.getPort());
    }

}
