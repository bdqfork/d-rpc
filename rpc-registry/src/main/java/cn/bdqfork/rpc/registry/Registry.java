package cn.bdqfork.rpc.registry;

import java.util.List;

/**
 * @author bdq
 * @date 2019-02-26
 */
public interface Registry {

    /**
     * 注册服务
     *
     * @param url
     */
    void register(URL url);

    /**
     * 注册服务
     *
     * @param urls
     */
    void register(List<URL> urls);

    /**
     * 订阅服务，同时返回服务节点信息
     *
     * @param url
     * @param notifier
     */
    void subscribe(URL url, Notifier notifier);

}
