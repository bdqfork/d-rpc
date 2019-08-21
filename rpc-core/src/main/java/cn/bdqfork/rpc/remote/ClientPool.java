package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionUtils;
import cn.bdqfork.rpc.exporter.RefreshCallback;
import cn.bdqfork.rpc.registry.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class ClientPool {
    private static final String ADDRESS_SEPARATOR = ":";
    private RemoteClientFactory remoteClientFactory = ExtensionUtils.getExtension(RemoteClientFactory.class);
    private Map<String, RemoteClient> clientMap = new ConcurrentHashMap<>();
    private RefreshCallback callback;
    private int i;

    public ClientPool(RefreshCallback callback) {
        this.callback = callback;
    }

    public void refresh(Set<String> remoteAddress) {
        Set<String> localAddress = clientMap.keySet();
        Set<URL> urls = remoteAddress.stream()
                .filter(address -> !clientMap.containsKey(address))
                .map(URL::new)
                .collect(Collectors.toSet());

        Set<String> keys = urls.stream()
                .map(url -> getKey(url.getHost(), url.getPort()))
                .collect(Collectors.toSet());
        //求差集
        localAddress.removeAll(keys);

        //移除过期的连接
        localAddress.forEach(address -> {
            RemoteClient remoteClient = clientMap.get(address);
            remoteClient.close();
            clientMap.remove(address);
        });

        urls.forEach(url -> {

            String server = url.getParameter(Const.SERVER_KEY, "netty");
            String serialization = url.getParameter(Const.SERIALIZATION_KEY, "hessian");

            RemoteClient remoteClient = remoteClientFactory.createRemoteClient(server, serialization,
                    url.getHost(), url.getPort());
            
            addRemoteClient(remoteClient);
        });
    }

    private void addRemoteClient(RemoteClient remoteClient) {
        clientMap.put(getKey(remoteClient.getHost(), remoteClient.getPort()), remoteClient);
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
        clientMap.remove(getKey(remoteClient.getHost(), remoteClient.getPort()));
    }

    private String getKey(String host, int port) {
        return host + ADDRESS_SEPARATOR + port;
    }

}
