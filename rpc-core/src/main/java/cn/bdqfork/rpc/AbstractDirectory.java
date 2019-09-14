package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
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
    protected volatile boolean isAvailable;
    protected Class<T> serviceInterface;
    protected URL url;
    protected List<URL> urls;

    public AbstractDirectory(Class<T> serviceInterface, URL url) {
        this.serviceInterface = serviceInterface;
        this.url = url;
        this.urls = Collections.emptyList();
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

    protected void addRpcInvoker(URL url) {
        mergeUrl(url);
        RemoteClient[] remoteClients = new RemoteClient[0];
        try {
            remoteClients = remoteClientFactory.createRemoteClients(url);
        } catch (RpcException e) {
            log.warn(e.getMessage(), e);
        }
        RpcInvoker<T> invoker = new RpcInvoker<>(serviceInterface, url);
        invoker.setRemoteClients(remoteClients);
        invokers.put(url.buildString(), invoker);
    }

    private void mergeUrl(URL url) {
        String timeout = this.url.getParameter(Const.TIMEOUT_KEY);
        url.addParameter(Const.TIMEOUT_KEY, timeout);
        String retries = this.url.getParameter(Const.RETRY_KEY);
        url.addParameter(Const.RETRY_KEY, retries);
        String connections = this.url.getParameter(Const.CONNECTIONS_KEY);
        url.addParameter(Const.CONNECTIONS_KEY, connections);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void destroy() {
        invokers.values().forEach(Node::destroy);
        isAvailable = false;
    }

}
