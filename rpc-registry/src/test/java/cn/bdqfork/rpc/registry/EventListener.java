package cn.bdqfork.rpc.registry;

import org.apache.zookeeper.WatchedEvent;

/**
 * @author bdq
 * @date 2019-02-27
 */
public class EventListener implements Notifier {
    @Override
    public void notify(URL url, RegistryEvent event) {
        WatchedEvent watchedEvent = ((ZkRegistryEvent) event).getWatchedEvent();
        System.out.println(watchedEvent.getPath() + "--->" + watchedEvent.getType().name());
    }
}
