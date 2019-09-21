package cn.bdqfork.common.extension;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ExtensionLoader {

    private static final String PREFIX = "META-INF/rpc/";

    private static final Map<String, ExtensionLoader> CACHES = new ConcurrentHashMap<>();

    private Map<String, Object> extensions;

    private ExtensionLoader(Class<?> clazz) throws Exception {
        this.extensions = new HashMap<>();
        this.load(clazz);
    }

    public static ExtensionLoader getExtensionLoader(Class<?> clazz) {
        String className = clazz.getName();
        if (!CACHES.containsKey(className)) {
            synchronized (ExtensionLoader.class) {
                if (!CACHES.containsKey(className)) {
                    ExtensionLoader extensionLoader;
                    try {
                        extensionLoader = new ExtensionLoader(clazz);
                    } catch (Exception e) {
                        throw new IllegalStateException("Fail to create ExtensionLoader with class named " + className, e);
                    }
                    CACHES.put(className, extensionLoader);
                }
            }
        }
        return CACHES.get(className);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(String extension) {
        if (extensions.containsKey(extension)) {
            return (T) extensions.get(extension);
        }
        throw new IllegalStateException("No extension named " + extension);
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getExtensions() {
        return (Map<String, T>) Collections.unmodifiableMap(extensions);
    }

    private void load(Class extension) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(PREFIX + extension.getName());
        if (url.getProtocol().equals("file")) {
            String filepath = url.getFile();
            File file = new File(filepath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                //过滤注释
                if (line.contains("#")){
                    line = line.substring(0, line.indexOf("#"));
                }
                String[] values = line.split("=");
                String name = values[0].trim();
                String impl = values[1].trim();
                if (extensions.containsKey(name)) {
                    throw new IllegalStateException("Duplicate extension named " + name);
                }
                Class<?> clazz = classLoader.loadClass(impl);
                extensions.put(name, clazz.newInstance());
            }
        } else if (url.getProtocol().equals("jar")) {
            URLConnection urlConnection = url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                //过滤注释
                if (line.contains("#")){
                    line = line.substring(0, line.indexOf("#"));
                }
                String[] values = line.split("=");
                String name = values[0].trim();
                String impl = values[1].trim();
                if (extensions.containsKey(name)) {
                    throw new IllegalStateException("Duplicate extension named " + name);
                }
                Class<?> clazz = classLoader.loadClass(impl);
                extensions.put(name, clazz.newInstance());
            }
        }
    }

}
