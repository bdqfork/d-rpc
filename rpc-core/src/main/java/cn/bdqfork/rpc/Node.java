package cn.bdqfork.rpc;

import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-09-02
 */
public interface Node<T> {
    URL getUrl();

    Class<T> getInterface();

    boolean isAvailable();
}
