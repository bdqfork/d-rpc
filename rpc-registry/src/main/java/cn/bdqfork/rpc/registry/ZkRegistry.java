package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.constant.Const;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import java.util.*;

/**
 * @author bdq
 * @date 2019-02-26
 */
public class ZkRegistry implements Registry, ZkClient {
    private static final String DEFAULT_ROOT = "rpc";
    private CuratorFramework client;

    public ZkRegistry(String connectionInfo) {
        RetryPolicy retryPolicy = new RetryForever(1000);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionInfo)
                .sessionTimeoutMs(60000)
                .connectionTimeoutMs(6000)
                .retryPolicy(retryPolicy)
                .build();
        client.getCuratorListenable().addListener(new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println(event.getType());
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
}
