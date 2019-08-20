package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.config.RegistryConfig;

/**
 * @author bdq
 * @since 2019-08-20
 */
public interface RegistryFactory {
    Registry createRegistry(RegistryConfig registryConfig);
}
