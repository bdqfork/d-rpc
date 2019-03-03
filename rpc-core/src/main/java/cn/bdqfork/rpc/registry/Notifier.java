package cn.bdqfork.rpc.registry;


import cn.bdqfork.rpc.registry.event.RegistryEvent;

/**
 * @author bdq
 * @date 2019-02-27
 */
public interface Notifier {
    /**
     * 节点变化回调方法
     *
     * @param url
     * @param event
     */
    void notify(URL url, RegistryEvent event);
}
