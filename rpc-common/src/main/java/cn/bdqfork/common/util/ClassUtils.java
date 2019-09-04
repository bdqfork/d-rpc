package cn.bdqfork.common.util;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ClassUtils {

    public static Class<?> getClass(String beanClassName, ClassLoader classLoader) throws ClassNotFoundException {
           return Class.forName(beanClassName, false, classLoader);
    }

}
