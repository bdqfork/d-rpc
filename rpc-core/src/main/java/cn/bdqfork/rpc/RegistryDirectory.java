package cn.bdqfork.rpc;

import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class RegistryDirectory<T> extends AbstractDirectory<T> implements Notifier {

    private Registry registry;

    public RegistryDirectory(Class<T> serviceInterface, URL url) {
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
        urls.stream()
                .filter(url -> invokers.containsKey(url.buildString()))
                .forEach(this::removeOldInvoker);

        urls.stream()
                .filter(url -> !invokers.containsKey(url.buildString()))
                .forEach(this::addRpcInvoker);
    }

}
