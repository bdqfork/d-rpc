package cn.bdqfork.rpc.registry.zookeeper;

import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.registry.AbstractRegistryFactory;
import cn.bdqfork.rpc.registry.Registry;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url);
    }
}
