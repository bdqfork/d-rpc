package cn.bdqfork.rpc.remote;

import cn.bdqfork.rpc.AbstractDirectory;
import cn.bdqfork.rpc.Node;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class Directory<T> extends AbstractDirectory<T> implements Notifier {

    private Registry registry;

    public Directory(Class<T> serviceInterface, URL url) {
        super(serviceInterface, url);
    }

    public URL getUrl() {
        return url;
    }

    @Override
    protected List<Invoker<T>> doList(Invocation invocation) {
        //路由条件筛选
        return new ArrayList<>(invokers.values());
    }

    public void subscribe() {
        registry.subscribe(url, this);
    }

    @Override
    protected void refresh() {
        List<URL> urls = registry.lookup(url);
        notify(urls);
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

    @Override
    public void notify(List<URL> urls) {
        if (urls == null || urls.isEmpty()) {
            isAvailable = false;
            this.invokers.values().forEach(Node::destroy);
            this.invokers.clear();
        } else {

            this.urls.removeAll(urls);
            this.urls.stream()
                    .map(URL::buildString)
                    .forEach(url -> invokers.remove(url).destroy());

            urls.stream()
                    .filter(url -> !invokers.containsKey(url.buildString()))
                    .forEach(this::addRpcInvoker);
            isAvailable = true;
        }
        this.urls = urls;
    }

}
