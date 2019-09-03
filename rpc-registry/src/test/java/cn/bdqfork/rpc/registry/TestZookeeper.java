package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.ApplicationConfig;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;
import org.junit.Test;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class TestZookeeper {
    @Test
    public void register() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setUrl("zookeeper://127.0.0.1:2181");
        Registry registry = new ZkRegistry(registryConfig);

        URL url = new URL(Const.PROTOCOL_PROVIDER, "127.0.0.1", 9000, "cn.registry.test");

        url.addParameter(Const.APPLICATION_KEY, "test");
        url.addParameter(Const.GROUP_KEY, "rpc");
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        url.addParameter(Const.REF_NAME_KEY, "test");
        url.addParameter(Const.SERVER_KEY, "netty");
        url.addParameter(Const.SERIALIZATION_KEY, "jdk");

        registry.register(url);

    }

}
