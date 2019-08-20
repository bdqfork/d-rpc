package cn.bdqfork.rpc.registry.event;

/**
 * @author bdq
 * @since 2019-02-28
 */
public interface RegistryEvent {
    /**
     * 获取事件名称
     *
     * @return
     */
    NodeEvent getEvent();
}
