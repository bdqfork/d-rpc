package cn.bdqfork.rpc.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class LocalRegistry {
    private Map<String, Object> serviceRegistry = new ConcurrentHashMap<>();

    public void register(String serviceInterface, Object instance) {
        serviceRegistry.put(serviceInterface, instance);
    }

    public Object lookup(String serviceInterface) {
        return serviceRegistry.get(serviceInterface);
    }
}
