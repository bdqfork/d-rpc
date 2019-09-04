package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.registry.AbstractRegistry;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-02-26
 */
public class ZkRegistry extends AbstractRegistry {
    private static final Logger log = LoggerFactory.getLogger(ZkRegistry.class);
    private CuratorFramework client;

    public ZkRegistry(RegistryConfig registryConfig) {
        RetryPolicy retryPolicy = new RetryNTimes(3, 1000);
        //获取zookeeper地址
        String url = registryConfig.getUrl().substring(12);
        client = CuratorFrameworkFactory.builder()
                .connectString(url)
                .sessionTimeoutMs(registryConfig.getSessionTimeout())
                .connectionTimeoutMs(registryConfig.getConnectionTimeout())
                .retryPolicy(retryPolicy)
                .build();

        client.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState.isConnected()) {
                running = true;
            } else {
                running = false;
                try {
                    client.blockUntilConnected();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
            recover();
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
    public void subscribe(URL url, Notifier notifier) {
        String group = url.getParameter(Const.GROUP_KEY, DEFAULT_ROOT);
        String path = "/" + group + url.toServiceCategory() + "/" + Const.PROTOCOL_PROVIDER;
        try {
            TreeCache treeCache = new TreeCache(client, path);
            treeCache.start();
            treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
                for (String children : client.getChildren().forPath(path)) {
                    if (isMatch(treeCacheEvent)) {
                        List<URL> urls = new ArrayList<>();
                        byte[] data = client.getData().forPath(path + "/" + children);
                        if (data != null) {
                            urls.add(new URL(new String(data, StandardCharsets.UTF_8)));
                        }
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
    public URL getUrl() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void destroy() {
        if (running) {
            client.close();
        }
        running = false;
    }
}
