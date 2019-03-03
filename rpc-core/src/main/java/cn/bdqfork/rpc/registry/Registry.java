package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.config.RegistryConfig;

import java.util.List;
import java.util.Set;

/**
 * @author bdq
 * @date 2019-02-26
 */
public interface Registry {

    /**
     * 初始化注册中心
     */
    void init();

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

    /**
     * 获取服务地址
     *
     * @param url
     * @return
     */
    Set<String> getServiceAddress(URL url);

    void setRegistryConfig(RegistryConfig registryConfig);

    void close();

}
