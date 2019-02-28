package cn.bdqfork.rpc.registry;


/**
 * @author bdq
 * @date 2019-02-27
 */
public interface Notifier {
    /**
     * 节点变化回掉方法
     *
     * @param url
     * @param event
     */
    void notify(URL url, RegistryEvent event);
}
