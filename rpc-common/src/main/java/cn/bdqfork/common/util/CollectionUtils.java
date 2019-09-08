package cn.bdqfork.common.util;

import java.util.Collection;

/**
 * @author bdq
 * @since 2019/9/8
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
