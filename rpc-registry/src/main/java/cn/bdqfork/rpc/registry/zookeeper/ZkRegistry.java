package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.common.constant.Const;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.Registry;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-02-26
 */
public class ZkRegistry implements Registry, ZkClient {
    private static final Logger log = LoggerFactory.getLogger(ZkRegistry.class);

    private static final String DEFAULT_ROOT = "rpc";
    private CuratorFramework client;
    private Map<String, URL> cacheNodeMap = new ConcurrentHashMap<>();
    private Map<String, CacheWatcher> cacheWatcherMap = new ConcurrentHashMap<>();

    public ZkRegistry(String connectionInfo, int sessionTimeout, int connectionTimeout) {
        RetryPolicy retryPolicy = new RetryNTimes(3, 1000);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionInfo)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(retryPolicy)
                .build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                recover();
            }
        });
        client.start();
    }

    @Override
    public void register(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toPath();
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path);
            }
            cacheNodeMap.putIfAbsent(path, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(List<URL> urls) {
        urls.forEach(this::register);
    }

    @Override
    public void subscribe(URL url, Notifier notifier) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toServicePath();
        try {
            if (client.checkExists().forPath(path) != null) {
                client.getChildren().usingWatcher(new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        notifier.notify(url, new ZkRegistryEvent(event));
                    }
                }).forPath(path);
                CacheWatcher cacheWatcher = new CacheWatcher(url, notifier);
                cacheWatcherMap.putIfAbsent(path, cacheWatcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getServiceAddress(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toServicePath();
        try {
            if (client.checkExists().forPath(path) != null) {
                return new HashSet<>(client.getChildren().forPath(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    @Override
    public void close() {
        client.close();
    }

    private void recover() {
        cacheNodeMap.values().forEach(this::register);
        cacheWatcherMap.values().forEach(cacheWatcher -> subscribe(cacheWatcher.url, cacheWatcher.notifier));
    }

    private class CacheWatcher {
        private URL url;
        private Notifier notifier;

        private CacheWatcher(URL url, Notifier notifier) {
            this.url = url;
            this.notifier = notifier;
        }

    }
}
