package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class DefaultRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        if (url.getProtocol().equals("zookeeper")) {
            return new ZkRegistry(url);
        }
        return null;
    }
}
