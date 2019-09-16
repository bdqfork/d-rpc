package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.filter.Filter;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-09-04
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {
    private static Logger log = LoggerFactory.getLogger(AbstractInvoker.class);
    private List<Filter> filters = ExtensionLoader.getExtensions(Filter.class);
    private boolean isAvailable = true;
    private AtomicBoolean destroyed = new AtomicBoolean(false);
    protected T proxy;
    protected Class<T> type;
    protected URL url;

    public AbstractInvoker(T proxy, Class<T> type, URL url) {
        this.proxy = proxy;
        this.type = type;
        this.url = url;
        sortFilters();
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        log.debug("filter entry ...");

        filters.forEach(filter -> filter.invoke(this, invocation));
        return doInvoke(invocation);
    }

    protected abstract Result doInvoke(Invocation invocation) throws RpcException;

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
        if (destroyed.compareAndSet(false, true)) {
            isAvailable = false;
        }
    }

    private void sortFilters() {
        this.filters = this.filters.stream()
                .sorted(Comparator.comparingInt(Filter::getOrder))
                .collect(Collectors.toList());
    }
}
