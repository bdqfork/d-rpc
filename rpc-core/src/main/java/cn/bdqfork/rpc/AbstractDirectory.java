package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-09-04
 */
public abstract class AbstractDirectory<T> implements Directory<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDirectory.class);
    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtension(RemoteClientFactory.class);
    protected Map<String, Invoker<T>> invokers = new ConcurrentHashMap<>();
    protected Class<T> serviceInterface;
    protected URL url;

    public AbstractDirectory(Class<T> serviceInterface, URL url) {
        this.serviceInterface = serviceInterface;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return serviceInterface;
    }

    @Override
    public List<Invoker<T>> list(Invocation invocation) {
        if (isAvailable()) {
            return new ArrayList<>(invokers.values());
        }
        refresh();
        return doList(invocation);
    }

    protected abstract List<Invoker<T>> doList(Invocation invocation);

    protected abstract void refresh();

    protected void removeOldInvoker(URL url) {
        invokers.get(url.buildString()).destroy();
        invokers.remove(url.buildString());
    }

    protected void addRpcInvoker(URL url) {
        int connections = Integer.parseInt(this.url.getParameter(Const.CONNECTIONS_KEY));
        RemoteClient[] remoteClients = new RemoteClient[connections];
        for (int i = 0; i < connections; i++) {
            try {
                remoteClients[i] = remoteClientFactory.createRemoteClient(url);
            } catch (RpcException e) {
                log.warn(e.getMessage());
            }
        }
        RpcInvoker<T> invoker = new RpcInvoker<>(serviceInterface, url);
        invoker.setRemoteClients(remoteClients);
        invokers.put(url.buildString(), invoker);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return invokers.size() > 0;
    }

    @Override
    public void destroy() {
        invokers.values().forEach(Node::destroy);
    }
}
