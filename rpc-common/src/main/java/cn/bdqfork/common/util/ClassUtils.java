package cn.bdqfork.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ClassUtils {

    public static Class<?> getClass(String beanClassName, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(beanClassName, false, classLoader);
    }

}
