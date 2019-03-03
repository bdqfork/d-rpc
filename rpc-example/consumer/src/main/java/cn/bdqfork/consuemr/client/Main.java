package cn.bdqfork.consuemr.client;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.proxy.RpcProxyFactory;
import cn.bdqfork.rpc.proxy.RpcProxyFactoryBean;
import cn.bdqfork.rpc.consumer.exchanger.Exchanger;
import cn.bdqfork.rpc.consumer.RpcInvoker;
import cn.bdqfork.rpc.proxy.ProxyType;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class Main {
    public static void main(String[] args) throws RpcException {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setHost("127.0.0.1");
        protocolConfig.setPort(8080);

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setClient(ZkRegistry.class.getName());
        registryConfig.setUrl("127.0.0.1:2181");
        Registry registry = new ZkRegistry();
        registry.setRegistryConfig(registryConfig);
        registry.init();

        Exchanger exchanger = new Exchanger(protocolConfig, registry);

        exchanger.register("rpc-test", UserService.class.getName());
        exchanger.subscribe("rpc-test", UserService.class.getName());

        ClientPool clientPool = exchanger.getClientPool("rpc-test", UserService.class.getName());
        RpcInvoker invoker = new RpcInvoker(clientPool, 100L, 3);

        RpcProxyFactory<UserService> rpcProxyFactory = new RpcProxyFactoryBean.Builder<UserService>()
                .invoker(invoker)
                .serviceInterface(UserService.class)
                .refName("userService")
                .build();

        UserService service = rpcProxyFactory.getProxy(ProxyType.JAVASSIST);

        while (true) {
            try {
                String userName = service.getUserName();
                service.sayHello(userName);
                Thread.sleep(1000);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }
}
