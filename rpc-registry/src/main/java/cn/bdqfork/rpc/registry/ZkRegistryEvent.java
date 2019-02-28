package cn.bdqfork.rpc.registry;

import org.apache.zookeeper.WatchedEvent;


/**
 * @author bdq
 * @date 2019-02-28
 */
public class ZkRegistryEvent implements RegistryEvent {
    private WatchedEvent watchEvent;

    public ZkRegistryEvent(WatchedEvent watchEvent) {
        this.watchEvent = watchEvent;
    }

    public WatchedEvent getWatchedEvent() {
        return watchEvent;
    }
}
