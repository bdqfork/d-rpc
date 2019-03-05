package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.config.RegistryConfig;

/**
 * @author bdq
 * @date 2019-03-03
 */
public abstract class AbstractRegistry implements Registry {

    private RegistryConfig registryConfig;

    public AbstractRegistry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    protected RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

}
