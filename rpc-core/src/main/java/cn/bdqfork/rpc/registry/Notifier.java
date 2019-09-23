package cn.bdqfork.rpc.registry;


import cn.bdqfork.common.URL;

import java.util.List;

/**
 * @author bdq
 * @since 2019-02-27
 */
public interface Notifier {
    /**
     * 通知方法
     *
     * @param urls
     */
    void notify(List<URL> urls);
}
