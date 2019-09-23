package cn.bdqfork.common.extension;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.util.StringUtils;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionLoader<T> {

    private static final String PREFIX = "META-INF/rpc/";

    private static final Map<String, ExtensionLoader> CACHES = new ConcurrentHashMap<>();

    private Class<T> type;
    private Class<T> adaptiveClass;
    private T adaptiveExtension;
    private String defaultName;
    private Map<Class<T>, String> classNames;
    private volatile Map<String, T> cacheExtensions;
    private volatile Map<String, Class<T>> extensionClasses;
    private volatile Map<String, Class<T>> activateClasses = new HashMap<>();

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

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

    public T getDefaultExtension() {
        String defaultName = getDefaultName();
        return getExtension(defaultName);
    }

    public T getExtension(String extensionName) {
        T extension = getExtensions().get(extensionName);
        if (extension != null) {
            return extension;
        }
        throw new IllegalStateException("No extension named " + extensionName);
    }

    public Map<String, T> getExtensions() {
        if (cacheExtensions == null) {
            synchronized (ExtensionLoader.class) {
                if (cacheExtensions == null) {
                    try {
                        createExtension();
                    } catch (Exception e) {
                        throw new IllegalStateException("Fail to create extensions for class " + getDefaultName() + "!", e);
                    }
                }
            }
        }
        return Collections.unmodifiableMap(cacheExtensions);
    }

    private void createExtension() throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        getExtensionClasses();
        cacheExtensions = new HashMap<>();

        for (Map.Entry<String, Class<T>> entry : extensionClasses.entrySet()) {
            Class<?> clazz = entry.getValue();
            cacheExtensions.putIfAbsent(entry.getKey(), (T) clazz.newInstance());
        }
    }

    private void getExtensionClasses() throws IOException, ClassNotFoundException {
        if (classNames != null && extensionClasses != null) {
            return;
        }
        classNames = new HashMap<>();
        extensionClasses = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        java.net.URL url = classLoader.getResource(PREFIX + type.getName());
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
                Class<T> clazz = (Class<T>) classLoader.loadClass(impl);
                if (clazz.isAnnotationPresent(Adaptive.class)) {
                    cacheAdaptiveClass(name, clazz);
                } else {
                    cacheActivateClass(name, clazz);
                }
                classNames.put(clazz, name);
                extensionClasses.put(name, clazz);
            }
        } else {
            throw new IllegalArgumentException("Extension path " + PREFIX + type.getName() + " don't exsist !");
        }
    }

    private void cacheActivateClass(String name, Class<T> clazz) {
        if (activateClasses == null) {
            activateClasses = new HashMap<>();
        }
        if (clazz.isAnnotationPresent(Activate.class)) {
            activateClasses.put(name, clazz);
        }
    }

    private void cacheAdaptiveClass(String name, Class<T> clazz) {
        if (adaptiveClass == null) {
            adaptiveClass = clazz;
        } else {
            throw new IllegalStateException("Duplicate @Adaptive extension " + name + "!");
        }
    }

    public T getAdaptiveExtension() {
        if (adaptiveExtension == null) {
            synchronized (ExtensionLoader.class) {
                if (adaptiveExtension == null) {
                    try {
                        createAdaptiveExtension();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return adaptiveExtension;
    }

    private void createAdaptiveExtension() {
        try {
            getExtensionClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (adaptiveClass != null) {
            String extensionName = classNames.get(adaptiveClass);
            adaptiveExtension = getExtension(extensionName);
            return;
        }
        String defaultName = getDefaultName();
        String code = new AdaptiveClassCodeGenerator(type, defaultName)
                .generate();
        Compiler compiler = ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();
        try {
            @SuppressWarnings("unchecked")
            Class<T> adaptiveClass = (Class<T>) compiler.compile(type.getCanonicalName() + "$Adaptive", code);
            adaptiveExtension = adaptiveClass.newInstance();
        } catch (CannotCompileException | NotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private String getDefaultName() {
        if (defaultName != null) {
            return defaultName;
        }
        Pattern pattern = Pattern.compile("[A-Z][a-z]+");
        SPI spi = type.getAnnotation(SPI.class);
        if (spi.value().isEmpty()) {
            StringBuilder defaultNameBuilder = new StringBuilder();
            Matcher matcher = pattern.matcher(type.getSimpleName());
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

    public List<T> getActivateExtensions(URL url, String groupName) {
        try {
            getExtensionClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<T> extensions = activateClasses.values()
                .stream()
                .filter(activateClass -> checkActive(url, groupName, activateClass))
                .sorted(Comparator.comparingInt((ToIntFunction<Class<T>>) value -> {
                    Activate activate = value.getAnnotation(Activate.class);
                    return activate.order();
                }).reversed())
                .map(activateClass -> classNames.get(activateClass))
                .map(this::getExtension)
                .collect(Collectors.toList());

        return extensions;
    }

    public T getActivateExtension(URL url, String extensionName, String groupName) {
        try {
            getExtensionClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (activateClasses.containsKey(extensionName)) {
            Class<T> activateClass = activateClasses.get(extensionName);
            Activate activate = activateClass.getAnnotation(Activate.class);
            if (activate != null && checkGroup(groupName, activate) && checkKey(url, activate)) {
                return getExtension(extensionName);
            }
        }
        throw new IllegalArgumentException("No extension named " + extensionName + " for group " + groupName + " !");
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
