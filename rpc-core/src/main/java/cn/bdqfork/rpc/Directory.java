package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.registry.event.NodeEvent;
import cn.bdqfork.rpc.registry.event.RegistryEvent;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class Directory<T> implements Node<T>,Notifier {
    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtension(RemoteClientFactory.class);
    private Registry registry;
    private Class<T> serviceInterface;
    private URL consumerUrl;
    private List<Invoker<T>> invokers = new CopyOnWriteArrayList<>();

    public Directory(Class<T> serviceInterface, URL url) throws RpcException {
        this.serviceInterface = serviceInterface;
        this.consumerUrl = url;
    }

    public URL getUrl() {
        return consumerUrl;
    }

    @Override
    public Class<T> getInterface() {
        return serviceInterface;
    }

    @Override
    public boolean isAvailable() {
        return invokers.size() > 0;
    }

    public List<Invoker<T>> list() throws RpcException {
        if (isAvailable()) {
            return invokers;
        }
        List<URL> urls = registry.lookup(consumerUrl);
        for (URL refUrl : urls) {
            RpcInvoker<T> invoker = new RpcInvoker<>(serviceInterface, refUrl);

            int connections = Integer.parseInt(consumerUrl.getParameter(Const.CONNECTIONS_KEY));
            RemoteClient[] remoteClients = new RemoteClient[connections];
            for (int i = 0; i < connections; i++) {
                remoteClients[i] = remoteClientFactory.createRemoteClient(refUrl);
            }
            invoker.setRemoteClients(remoteClients);

            invokers.add(invoker);
        }
        return invokers;
    }

    @Override
    public void notify(URL url, RegistryEvent event) throws RpcException {
        if (NodeEvent.CREATED == event.getEvent() || NodeEvent.CHANGED == event.getEvent()) {
            refresh();
        }
    }

    public void refresh() throws RpcException {
        registry.subscribe(consumerUrl, this);
        invokers.clear();
        list();
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Class<T> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

}
