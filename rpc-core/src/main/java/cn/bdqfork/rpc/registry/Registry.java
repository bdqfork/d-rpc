package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.Node;

import java.util.List;

/**
 * @author bdq
 * @since 2019-02-26
 */
public interface Registry extends Node {
    String DEFAULT_ROOT = "rpc";

    /**
     * 是否在运行
     *
     * @return boolean
     */
    boolean isRunning();

    /**
     * 注册服务
     *
     * @param url
     */
    void register(URL url);

    /**
     * 订阅服务
     *
     * @param url
     */
    void subscribe(URL url, Notifier notifier);

    /**
     * 返回服务节点信息
     *
     * @param url
     * @return
     */
    List<URL> lookup(URL url);

}
