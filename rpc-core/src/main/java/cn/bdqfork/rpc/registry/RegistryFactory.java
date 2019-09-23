package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;

/**
 * @author bdq
 * @since 2019-08-20
 */
@SPI("zookeeper")
public interface RegistryFactory {
    @Adaptive
    Registry getRegistry(URL url);
}
