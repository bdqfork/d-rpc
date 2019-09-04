package cn.bdqfork.rpc;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class AvailableCluster implements Cluster {
    @Override
    public <T> Invoker<T> join(RegistryDirectory<T> registryDirectory) {
        return new AvailableClusterInvoker<>(registryDirectory);
    }
}
