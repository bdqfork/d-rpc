package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.registry.etcd.EtcdRegistry;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class DefaultRegistryFactory implements RegistryFactory {
    @Override
    public Registry createRegistry(RegistryConfig registryConfig) {
        String url = registryConfig.getUrl();
        if (url.startsWith("zookeeper://")) {
            return new ZkRegistry(registryConfig);
        } else if (url.startsWith("etcd://")) {
            return new EtcdRegistry(registryConfig);
        }
        return null;
    }
}
