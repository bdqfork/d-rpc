package cn.bdqfork.common.extension;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.compiler.AdaptiveClassCodeGenerator;
import cn.bdqfork.common.extension.compiler.Compiler;
import cn.bdqfork.common.util.ClassUtils;
import cn.bdqfork.common.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionLoader<T> {
    private static final String PREFIX = "META-INF/rpc/";
    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Z][a-z]+");
    private static final Map<String, ExtensionLoader> CACHES = new ConcurrentHashMap<>();
    private final Map<Class<T>, String> classNames = new ConcurrentHashMap<>();
    private final Map<String, Class<T>> extensionClasses = new ConcurrentHashMap<>();
    private final Map<String, Class<T>> activateClasses = new ConcurrentHashMap<>();

    private volatile Class<T> adaptiveClass;
    private volatile T adaptiveExtension;
    private volatile Map<String, T> cacheExtensions;
    private Class<T> type;
    private String defaultName;

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    /**
     * 获取扩展接口对应的ExtensionLoader
     *
     * @param clazz 扩展接口
     * @param <T>   Class类型
     * @return ExtensionLoader<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        String className = clazz.getName();

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Fail to create ExtensionLoader for class " + className
                    + ", class is not Interface !");
        }

        SPI spi = clazz.getAnnotation(SPI.class);

        if (spi == null) {
            throw new IllegalArgumentException("Fail to create ExtensionLoader for class " + className
                    + ", class is not annotated by @SPI !");
        }

        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) CACHES.get(className);

        if (extensionLoader == null) {
            CACHES.putIfAbsent(className, new ExtensionLoader<>(clazz));
            extensionLoader = (ExtensionLoader<T>) CACHES.get(className);
        }

        return extensionLoader;
    }

    /**
     * 获取默认扩展
     *
     * @return T
     */
    public T getDefaultExtension() {
        String defaultName = getDefaultName();
        return getExtension(defaultName);
    }

    /**
     * 根据extensionName获取扩展实例
     *
     * @param extensionName 扩展名称
     * @return T
     */
    public T getExtension(String extensionName) {
        T extension = getExtensions().get(extensionName);
        if (extension != null) {
            return extension;
        }
        throw new IllegalStateException("No extension named " + extensionName + " for class " + type.getName() + "!");
    }

    /**
     * 获取所有扩展
     *
     * @return Map<String, T>
     */
    public Map<String, T> getExtensions() {
        if (cacheExtensions == null) {
            cacheExtensions = new ConcurrentHashMap<>();
            getExtensionClasses();

            for (Map.Entry<String, Class<T>> entry : extensionClasses.entrySet()) {
                Class<T> clazz = entry.getValue();
                cacheExtensions.putIfAbsent(entry.getKey(), ClassUtils.newInstance(clazz));
            }

        }
        return Collections.unmodifiableMap(cacheExtensions);
    }

    private void getExtensionClasses() {
        if (classNames.size() > 0 && classNames.size() > 0) {
            return;
        }
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<java.net.URL> urlEnumeration = classLoader.getResources(PREFIX + type.getName());
            while (urlEnumeration.hasMoreElements()) {
                java.net.URL url = urlEnumeration.nextElement();
                if (url.getPath().isEmpty()) {
                    throw new IllegalArgumentException("Extension path " + PREFIX + type.getName() + " don't exsist !");
                }
                if (url.getProtocol().equals("file") || url.getProtocol().equals("jar")) {
                    URLConnection urlConnection = url.openConnection();
                    Reader reader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.equals("")) {
                            continue;
                        }
                        //过滤注释
                        if (line.contains("#")) {
                            line = line.substring(0, line.indexOf("#"));
                        }
                        String[] values = line.split("=");
                        String name = values[0].trim();
                        String impl = values[1].trim();
                        if (extensionClasses.containsKey(name)) {
                            throw new IllegalStateException("Duplicate extension named " + name);
                        }
                        @SuppressWarnings("unchecked")
                        Class<T> clazz = (Class<T>) classLoader.loadClass(impl);

                        if (clazz.isAnnotationPresent(Adaptive.class)) {
                            cacheAdaptiveClass(name, clazz);
                        } else if (clazz.isAnnotationPresent(Activate.class)) {
                            activateClasses.putIfAbsent(name, clazz);
                        }

                        classNames.putIfAbsent(clazz, name);
                        extensionClasses.putIfAbsent(name, clazz);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Fail to get extension class from " + PREFIX + type.getName() + "!", e);
        }
    }

    private void cacheAdaptiveClass(String name, Class<T> clazz) {
        if (adaptiveClass == null) {
            adaptiveClass = clazz;
        } else {
            throw new IllegalStateException("Duplicate @Adaptive extension " + name + "!");
        }
    }

    /**
     * 获取Adaptive扩展
     *
     * @return T
     */
    public T getAdaptiveExtension() {
        if (adaptiveExtension == null) {
            createAdaptiveExtension();
        }
        return adaptiveExtension;
    }

    private void createAdaptiveExtension() {
        getExtensionClasses();

        if (adaptiveClass != null) {
            String extensionName = classNames.get(adaptiveClass);
            adaptiveExtension = getExtension(extensionName);
            return;
        }

        String defaultName = getDefaultName();

        String code = new AdaptiveClassCodeGenerator(type, defaultName)
                .generate();
        Compiler compiler = ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();

        @SuppressWarnings("unchecked")
        Class<T> adaptiveClass = (Class<T>) compiler.compile(code, getClass().getClassLoader());

        adaptiveExtension = ClassUtils.newInstance(adaptiveClass);
    }

    private String getDefaultName() {
        if (defaultName != null) {
            return defaultName;
        }
        SPI spi = type.getAnnotation(SPI.class);
        if (spi.value().isEmpty()) {
            Matcher matcher = NAME_PATTERN.matcher(type.getSimpleName());
            StringBuilder defaultNameBuilder = new StringBuilder();
            while (matcher.find()) {
                defaultNameBuilder.append(matcher.group())
                        .append(".");
            }
            StringUtils.removeLastChar(defaultNameBuilder);
            defaultName = StringUtils.lowerFirst(defaultNameBuilder.toString());
        } else {
            defaultName = spi.value();
        }
        return defaultName;
    }

    /**
     * 根据url和group获取Activate集合扩展
     *
     * @param url       url
     * @param groupName 分组
     * @return List<T>
     */
    public List<T> getActivateExtensions(URL url, String groupName) {
        getExtensionClasses();
        return activateClasses.values()
                .stream()
                .filter(activateClass -> checkActive(url, groupName, activateClass))
                .sorted(Comparator.comparingInt(this::getOrder).reversed())
                .map(classNames::get)
                .map(this::getExtension)
                .collect(Collectors.toList());
    }

    private int getOrder(Class<T> value) {
        Activate activate = value.getAnnotation(Activate.class);
        return activate.order();
    }

    /**
     * 根据url，extensionName和group获取Activate集合扩展
     *
     * @param url           url
     * @param extensionName 扩展名
     * @param groupName     分组
     * @return T
     */
    public T getActivateExtension(URL url, String extensionName, String groupName) {
        getExtensionClasses();

        if (activateClasses.containsKey(extensionName)) {
            Class<T> activateClass = activateClasses.get(extensionName);

            Activate activate = activateClass.getAnnotation(Activate.class);

            if (activate != null && checkGroup(groupName, activate) && checkKey(url, activate)) {
                return getExtension(extensionName);
            }

        }
        throw new IllegalArgumentException("No extension named " + extensionName + " and group by " + groupName +
                " for class " + type.getCanonicalName() + "!");
    }

    private boolean checkActive(URL url, String groupName, Class<T> activateClass) {
        Activate activate = activateClass.getAnnotation(Activate.class);
        return checkGroup(groupName, activate) && checkKey(url, activate);
    }

    private boolean checkKey(URL url, Activate activate) {
        if (activate.value().length == 0) {
            return true;
        }
        for (String key : activate.value()) {
            if (url.hasParameter(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGroup(String groupName, Activate activate) {
        for (String group : activate.group()) {
            if (group.equals(groupName)) {
                return true;
            }
        }
        return false;
    }

}
