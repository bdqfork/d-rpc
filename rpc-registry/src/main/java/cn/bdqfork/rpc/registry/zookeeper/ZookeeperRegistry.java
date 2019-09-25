package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.registry.AbstractRegistry;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.common.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-02-26
 */
public class ZookeeperRegistry extends AbstractRegistry {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private Map<String, URL> cacheNodes = new ConcurrentHashMap<>();
    private Map<String, CacheWatcher> cacheWatchers = new ConcurrentHashMap<>();
    private CuratorFramework client;

    public ZookeeperRegistry(URL url) {
        super(url);
    }

    @Override
    protected void doConnect() {
        RetryPolicy retryPolicy = new RetryNTimes(3, 1000);
        //获取zookeeper地址
        String hostUrl = url.getParameter(Const.REGISTRY_KEY);
        int seesionTimeout = Integer.parseInt(url.getParameter(Const.SEESION_TIMEOUT_KEY));
        int connectionTimeout = Integer.parseInt(url.getParameter(Const.CONNECTION_TIMEOUT_KEY));

        client = CuratorFrameworkFactory.builder()
                .connectString(hostUrl)
                .sessionTimeoutMs(seesionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(retryPolicy)
                .build();

        client.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState.isConnected()) {
                isAvailable = true;
                recover();
            } else if (!destroyed.get()) {
                isAvailable = false;
                try {
                    client.blockUntilConnected();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        client.start();
    }

    private void recover() {
        cacheNodes.values().forEach(this::register);
        cacheWatchers.values().forEach(watcher -> subscribe(watcher.getUrl(), watcher.getNotifier()));
    }

    @Override
    public void register(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toPath();
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(path, url.buildString().getBytes(StandardCharsets.UTF_8));
            cacheNodes.putIfAbsent(path, url);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void undoRegister(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toPath();
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void subscribe(URL url, Notifier notifier) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toServiceCategory() + "/" + Const.PROTOCOL_PROVIDER;
        try {
            TreeCache treeCache = new TreeCache(client, path);
            treeCache.start();
            treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
                if (isMatch(treeCacheEvent)) {

                    Map<String, ChildData> childDataMap = treeCache.getCurrentChildren(path);
                    if (childDataMap == null) {
                        notifier.notify(Collections.emptyList());
                    } else {
                        List<URL> urls = childDataMap.values()
                                .stream()
                                .map(childData -> new String(childData.getData(), StandardCharsets.UTF_8))
                                .map(URL::new)
                                .collect(Collectors.toList());
                        notifier.notify(urls);
                    }
                }
            });
            cacheWatchers.putIfAbsent(path, new CacheWatcher(url, notifier));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isMatch(TreeCacheEvent treeCacheEvent) {
        log.debug("zookeeper notify event: {} !", treeCacheEvent.getType());
        return treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED || treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED ||
                treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED;
    }

    @Override
    public List<URL> lookup(URL url) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toServiceCategory() + "/" + Const.PROTOCOL_PROVIDER;
        try {
            if (client.checkExists().forPath(path) != null) {
                List<URL> urls = new LinkedList<>();
                for (String children : client.getChildren().forPath(path)) {
                    byte[] data = client.getData().forPath(path + "/" + children);
                    if (data != null) {
                        urls.add(new URL(new String(data, StandardCharsets.UTF_8)));
                    }
                }
                return urls;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    protected void doDestroy() {
        client.close();
    }

    class CacheWatcher {
        private URL url;
        private Notifier notifier;

        CacheWatcher(URL url, Notifier notifier) {
            this.url = url;
            this.notifier = notifier;
        }

        URL getUrl() {
            return url;
        }

        Notifier getNotifier() {
            return notifier;
        }
    }
}
