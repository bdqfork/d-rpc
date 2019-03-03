package cn.bdqfork.provider.config;

import cn.bdqfork.rpc.config.ApplicationConfig;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.config.annotation.RpcComponentScan;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author bdq
 * @date 2019-03-03
 */
@ComponentScan
@RpcComponentScan(basePackages = "cn.bdqfork")
public class RpcConfigration {

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig config = new ApplicationConfig();
        config.setApplicationName("test");
        return config;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setClient(ZkRegistry.class.getName());
        registryConfig.setUrl("127.0.0.1:2181");
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setHost("127.0.0.1");
        protocolConfig.setPort(8081);
        return protocolConfig;
    }
}
