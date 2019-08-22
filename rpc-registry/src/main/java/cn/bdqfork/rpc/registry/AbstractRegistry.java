package cn.bdqfork.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-03
 */
public abstract class AbstractRegistry implements Registry {
    protected Map<String, URL> cacheNodeMap = new ConcurrentHashMap<>();

    protected Map<String, CacheWatcher> cacheWatcherMap = new ConcurrentHashMap<>();

    protected boolean running;

    @Override
    public boolean isRunning() {
        return running;
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
