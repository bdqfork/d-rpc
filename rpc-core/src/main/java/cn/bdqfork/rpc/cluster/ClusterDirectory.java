package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.context.AbstractDirectory;
import cn.bdqfork.rpc.Node;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.URL;
import cn.bdqfork.rpc.context.remote.*;
import org.apache.commons.lang3.StringUtils;
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
    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtensionLoader(RemoteClientFactory.class)
            .getExtension("default");

    private List<Registry> registries;

    public ClusterDirectory(Class<T> serviceInterface, URL url) {
        super(serviceInterface, url);
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
            log.debug("Destroy all invokers !");
            this.invokers.values().forEach(Node::destroy);
            this.invokers.clear();
        } else {
            log.debug("Update invokers !");
            urls.forEach(this::mergeUrl);

            this.urls.removeAll(urls);
            this.urls.stream()
                    .map(URL::buildString)
                    .forEach(url -> invokers.remove(url).destroy());
            urls.stream()
                    .filter(this::isMatch)
                    .forEach(this::addRpcInvoker);
        }
        this.urls = urls;
    }

    private boolean isMatch(URL url) {
        if (StringUtils.isBlank(this.version)) {
            return !invokers.containsKey(url.buildString());
        }
        return url.getParameter(Const.VERSION_KEY).equals(this.version)
                && !invokers.containsKey(url.buildString());
    }

    private void addRpcInvoker(URL url) {
        RemoteClient[] remoteClients = new RemoteClient[0];
        try {
            remoteClients = remoteClientFactory.getRemoteClients(url);
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
        boolean isAsync = this.url.getParameter(Const.ASYNC_KEY);
        url.addParameter(Const.ASYNC_KEY, isAsync);
        url.addParameter(Const.VERSION_KEY, this.version);
    }

    public void setRegistries(List<Registry> registries) {
        this.registries = registries;
    }

}
