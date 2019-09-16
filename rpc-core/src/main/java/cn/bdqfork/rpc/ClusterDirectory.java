package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class ClusterDirectory<T> extends AbstractDirectory<T> implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(ClusterDirectory.class);
    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtension(RemoteClientFactory.class);

    private List<Registry> registries;

    public ClusterDirectory(Class<T> serviceInterface, URL url) {
        super(serviceInterface, url);
    }

    public URL getUrl() {
        return url;
    }

    @Override
    protected List<Invoker<T>> doList(Invocation invocation) {
        //TODO:根据Invocation作路由条件筛选
        return new ArrayList<>(invokers.values());
    }

    public void subscribe() {
        registries.forEach(registry -> registry.subscribe(url, this));
    }

    @Override
    protected void refresh() {
        log.debug("directory refresh !");
        List<URL> urls = registries.stream()
                .map(registry -> registry.lookup(url))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        notify(urls);
    }

    @Override
    public void notify(List<URL> urls) {
        log.debug("directory notified with url size {} !", urls.size());
        if (destroyed.get()) {
            return;
        }
        if (urls == null || urls.isEmpty()) {
            log.info("Destroy all invokers !");
            this.invokers.values().forEach(Node::destroy);
            this.invokers.clear();
        } else {
            log.info("Update invokers !");
            urls.forEach(this::mergeUrl);

            this.urls.removeAll(urls);
            this.urls.stream()
                    .map(URL::buildString)
                    .forEach(url -> invokers.remove(url).destroy());
            urls.stream()
                    .filter(url -> !invokers.containsKey(url.buildString()))
                    .forEach(this::addRpcInvoker);
        }
        this.urls = urls;
    }

    private void addRpcInvoker(URL url) {
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
        String refName = this.url.getParameter(Const.REF_NAME_KEY);
        url.addParameter(Const.REF_NAME_KEY, refName);
    }

    public void setRegistries(List<Registry> registries) {
        this.registries = registries;
    }

}
