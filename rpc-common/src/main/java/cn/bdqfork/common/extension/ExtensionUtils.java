package cn.bdqfork.common.extension;

import cn.bdqfork.common.util.ClassUtils;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionUtils {

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

}
