package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.rpc.registry.RegistryEvent;
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

    @Override
    public String getEvent() {
        return watchEvent.getType().name();
    }

}
