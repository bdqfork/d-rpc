package cn.bdqfork.common.extension;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionLoader {

    private static final Map<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * 使用SPI获取扩展
     *
     * @param extension 扩展类
     * @param <T>       类型
     * @return T
     */
    public static <T> T getExtension(Class<T> extension) {
        String extensionName = extension.getName();
        if (cache.containsKey(extensionName)) {
            return (T) cache.get(extensionName);
        }
        ServiceLoader<T> serviceLoader = ServiceLoader.load(extension);
        T instance = null;
        for (T t : serviceLoader) {
            instance = t;
            break;
        }
        cache.put(extensionName, instance);
        return instance;
    }

    /**
     * 使用SPI获取扩展
     *
     * @param extension 扩展类
     * @param <T>       类型
     * @return T
     */
    public static <T> List<T> getExtensions(Class<T> extension) {
        String extensionName = extension.getName();
        if (cache.containsKey(extensionName)) {
            return (List<T>) cache.get(extensionName);
        }
        List<T> extensions = new LinkedList<>();
        ServiceLoader<T> serviceLoader = ServiceLoader.load(extension);
        for (T t : serviceLoader) {
            extensions.add(t);
        }
        cache.put(extensionName, extensions);
        return extensions;
    }

}
