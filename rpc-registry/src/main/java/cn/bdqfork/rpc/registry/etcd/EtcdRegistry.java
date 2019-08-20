package cn.bdqfork.rpc.registry.etcd;

import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.registry.AbstractRegistry;
import cn.bdqfork.rpc.registry.Notifier;
import cn.bdqfork.rpc.registry.URL;

import java.util.List;
import java.util.Set;

/**
 * @author bdq
 * @since 2019-08-20
 */
public class EtcdRegistry extends AbstractRegistry {
    public EtcdRegistry(RegistryConfig registryConfig) {
    }

    @Override
    public void register(URL url) {

    }

    @Override
    public void register(List<URL> urls) {

    }

    @Override
    public void subscribe(URL url, Notifier notifier) {

    }

    @Override
    public Set<String> getServiceAddress(URL url) {
        return null;
    }

    @Override
    public void close() {

    }
}
