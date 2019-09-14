package cn.bdqfork.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-03
 */
public abstract class AbstractRegistry implements Registry {
    public static final String DEFAULT_ROOT = "rpc";
    protected Map<String, URL> cacheNodes = new ConcurrentHashMap<>();
    protected Map<String, CacheWatcher> cacheWatchers = new ConcurrentHashMap<>();
    private URL url;
    protected volatile boolean running;

    public AbstractRegistry(URL url) {
        this.url = url;
    }

    @Override
    public boolean isAvailable() {
        return running;
    }

    @Override
    public void destroy() {
        running = false;
        doDestroy();
    }

    protected abstract void doDestroy();


    @Override
    public URL getUrl() {
        return url;
    }

    protected class CacheWatcher {
        private URL url;
        private Notifier notifier;

        public CacheWatcher(URL url, Notifier notifier) {
            this.url = url;
            this.notifier = notifier;
        }

        public URL getUrl() {
            return url;
        }

        public Notifier getNotifier() {
            return notifier;
        }
    }

}
