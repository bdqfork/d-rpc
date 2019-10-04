package cn.bdqfork.rpc.context;

import cn.bdqfork.common.Node;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.Directory;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2019-09-04
 */
public abstract class AbstractDirectory<T> implements Directory<T> {
    protected Map<String, Invoker<T>> invokers = new ConcurrentHashMap<>();
    protected AtomicBoolean destroyed = new AtomicBoolean(false);
    protected Class<T> serviceInterface;
    protected URL url;
    protected String version;
    protected List<URL> urls;

    public AbstractDirectory(Class<T> serviceInterface, URL url) {
        this.serviceInterface = serviceInterface;
        this.url = url;
        this.version = url.getParameter(Const.VERSION_KEY);
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

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        if (destroyed.get()) {
            return false;
        }
        return invokers.size() > 0;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            invokers.values().forEach(Node::destroy);
            invokers.clear();
        }
    }

}
