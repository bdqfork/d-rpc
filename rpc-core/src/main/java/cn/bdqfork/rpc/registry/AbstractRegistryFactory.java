package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bdq
 * @since 2019/9/13
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {
    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final Map<String, Registry> REGISTRIES = new HashMap<>();

    @Override
    public Registry getRegistry(URL url) {
        String key = url.buildString();
        LOCK.lock();
        try {
            Registry registry = REGISTRIES.get(key);
            if (registry == null) {
                registry = createRegistry(url);
            }
            if (registry == null) {
                throw new IllegalStateException();
            }
            REGISTRIES.put(key, registry);
            return registry;
        } finally {
            LOCK.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);
}
