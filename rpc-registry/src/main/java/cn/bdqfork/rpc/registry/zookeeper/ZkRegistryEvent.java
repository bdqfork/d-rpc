package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.rpc.registry.event.NodeEvent;
import cn.bdqfork.rpc.registry.event.RegistryEvent;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


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
    public NodeEvent getEvent() {
        if (Watcher.Event.EventType.NodeCreated == watchEvent.getType()) {
            return NodeEvent.CREATED;
        }
        if (Watcher.Event.EventType.NodeDataChanged == watchEvent.getType() || Watcher.Event.EventType.NodeChildrenChanged == watchEvent.getType()) {
            return NodeEvent.CHANGED;
        }
        if (Watcher.Event.EventType.NodeDeleted == watchEvent.getType()) {
            return NodeEvent.DELETED;
        }
        return NodeEvent.NONE;
    }

}
