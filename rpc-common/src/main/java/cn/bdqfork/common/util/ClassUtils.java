package cn.bdqfork.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ClassUtils {

    public static final String JAVA_EXTENSION = ".java";

    public static Class<?> getClass(String beanClassName, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(beanClassName, false, classLoader);
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't instantiate class " + clazz.getName() + "!");
        }
    }

    public static URI toURI(String baseName) {
        try {
            return new URI(baseName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName() + ": ");
        if (e.getMessage() != null) {
            p.print(e.getMessage() + "\n");
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
}
