package cn.bdqfork.consuemr.client;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.consumer.config.Configration;
import cn.bdqfork.rpc.consumer.remote.Exchanger;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.proxy.DefaultProxyFactory;
import cn.bdqfork.rpc.consumer.invoker.LocalInvoker;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.ZkRegistry;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class Main {
    public static void main(String[] args) {
        Configration configration = new Configration();
        configration.setHost("127.0.0.1");
        configration.setPort(8080);
        Registry registry = new ZkRegistry("127.0.0.1:2181", 60, 60);

        Exchanger exchanger = new Exchanger(configration, registry);

        exchanger.register("rpc-test", UserService.class.getName());
        exchanger.subscribe("rpc-test", UserService.class.getName());

        LocalInvoker invoker = new LocalInvoker(exchanger, 100L, 3);
        invoker.setGroup("rpc-test");

        ProxyFactory proxyFactory = new DefaultProxyFactory();

        UserService service = proxyFactory.getRemoteProxyInstance(invoker, UserService.class, "userService");

        while (true){
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
