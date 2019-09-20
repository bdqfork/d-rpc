package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.URL;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2019-03-03
 */
public abstract class AbstractRegistry implements Registry {
    public static final String DEFAULT_ROOT = "rpc";
    protected URL url;
    protected volatile boolean isAvailable;
    protected AtomicBoolean destroyed = new AtomicBoolean(false);

    public AbstractRegistry(URL url) {
        this.url = url;
        doConnect();
    }

    protected abstract void doConnect();

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            isAvailable = false;
            doDestroy();
        }
    }

    protected abstract void doDestroy();


    @Override
    public URL getUrl() {
        return url;
    }

}
