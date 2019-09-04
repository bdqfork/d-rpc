package cn.bdqfork.rpc;

/**
 * @author bdq
 * @since 2019-08-28
 */
public interface Cluster {
   <T> Invoker<T> join(RegistryDirectory<T> registryDirectory);
}
