package cn.bdqfork.rpc.provider;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class LocalRegistry {
    private static ConcurrentHashMap<String, Object> serviceRegistry = new ConcurrentHashMap<>();

    public static void register(String serviceInterface, Object instance) {
        serviceRegistry.put(serviceInterface, instance);
    }

    public static Object lookup(String serviceInterface) {
        return serviceRegistry.get(serviceInterface);
    }
}
