package cn.bdqfork.rpc.registry;

import cn.bdqfork.rpc.URL;

/**
 * @author bdq
 * @since 2019-08-20
 */
public interface RegistryFactory {
    Registry getRegistry(URL url);
}
