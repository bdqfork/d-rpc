package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/9/13
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Map<String, Registry> REGISTRIES = new ConcurrentHashMap<>();

    @Override
    public Registry getRegistry(URL url) {
        String key = url.buildString();
        Registry registry = REGISTRIES.get(key);
        if (registry == null) {
            registry = createRegistry(url);
        }
        if (registry == null) {
            throw new IllegalStateException();
        }
        REGISTRIES.putIfAbsent(key, registry);
        return registry;
    }

    protected abstract Registry createRegistry(URL url);
}
