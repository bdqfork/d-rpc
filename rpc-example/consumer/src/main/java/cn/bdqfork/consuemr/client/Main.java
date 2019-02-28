package cn.bdqfork.consuemr.client;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.proxy.DefaultProxyFactory;
import cn.bdqfork.rpc.consumer.invoker.LocalInvoker;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.client.NettyClient;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class Main {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8081);
        try {
            nettyClient.open();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Invoker<Object> invoker = new LocalInvoker(nettyClient,10000000L);
        ProxyFactory proxyFactory = new DefaultProxyFactory();
        UserService service = proxyFactory.getRemoteProxyInstance(invoker, UserService.class, "userService");
        String userName = service.getUserName();
        while (true){
            service.sayHello(userName);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
