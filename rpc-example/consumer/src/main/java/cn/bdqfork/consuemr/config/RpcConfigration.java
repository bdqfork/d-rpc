package cn.bdqfork.consuemr.config;

import cn.bdqfork.rpc.config.ApplicationConfig;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bdq
 * @date 2019-03-04
 */
@Configuration
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
        registryConfig.setUrl("zookeeper://127.0.0.1:2181");
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setHost("127.0.0.1");
        protocolConfig.setPort(8080);
        return protocolConfig;
    }
}
