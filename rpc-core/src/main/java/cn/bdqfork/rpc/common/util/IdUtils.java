package cn.bdqfork.rpc.common.util;

import java.util.UUID;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class IdUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
