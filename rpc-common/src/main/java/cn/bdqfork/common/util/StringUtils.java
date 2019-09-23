package cn.bdqfork.common.util;

/**
 * @author bdq
 * @since 2019/9/23
 */
public class StringUtils {
    public static String lowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public static void removeLastChar(StringBuilder stringBuilder) {
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }
}
