package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.registry.AbstractRegistryFactory;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        if (url.getProtocol().equals("zookeeper")) {
            return new ZkRegistry(url);
        }
        return null;
    }
}
