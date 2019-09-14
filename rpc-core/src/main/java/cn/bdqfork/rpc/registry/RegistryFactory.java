package cn.bdqfork.rpc.registry;

/**
 * @author bdq
 * @since 2019-08-20
 */
public interface RegistryFactory {
    Registry getRegistry(URL url);
}
