package cn.bdqfork.consuemr.client;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.consumer.config.Configration;
import cn.bdqfork.rpc.consumer.proxy.ProxyFactory;
import cn.bdqfork.rpc.consumer.proxy.ProxyFactoryBean;
import cn.bdqfork.rpc.consumer.exchanger.Exchanger;
import cn.bdqfork.rpc.consumer.invoker.RpcInvoker;
import cn.bdqfork.rpc.consumer.proxy.ProxyType;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.zookeeper.ZkRegistry;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class Main {
    public static void main(String[] args) throws RpcException {
        Configration configration = new Configration();
        configration.setHost("127.0.0.1");
        configration.setPort(8080);
        Registry registry = new ZkRegistry("127.0.0.1:2181", 60, 60);

        Exchanger exchanger = new Exchanger(configration, registry);

        exchanger.register("rpc-test", UserService.class.getName());
        exchanger.subscribe("rpc-test", UserService.class.getName());

        ClientPool clientPool = exchanger.getClientPool("rpc-test", UserService.class.getName());
        RpcInvoker invoker = new RpcInvoker(clientPool, 100L, 3);

        ProxyFactory<UserService> proxyFactory = new ProxyFactoryBean.Builder<UserService>()
                .invoker(invoker)
                .serviceInterface(UserService.class)
                .refName("userService")
                .build();

        UserService service = proxyFactory.getProxy(ProxyType.JAVASSIST);

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
