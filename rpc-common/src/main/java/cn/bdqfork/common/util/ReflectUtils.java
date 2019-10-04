package cn.bdqfork.common.util;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ReflectUtils {
    public static String getSignature(Method method) {
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(method.getName())
                .append("(");
        Class<?>[] parameters = method.getParameterTypes();

        for (Class<?> parameter : parameters) {
            signBuilder.append(parameter.getName())
                    .append(",");
        }
        StringUtils.removeLastChar(signBuilder);
        signBuilder.append(")");
        return signBuilder.toString();
    }

}
