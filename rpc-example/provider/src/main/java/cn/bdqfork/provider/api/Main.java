package cn.bdqfork.provider.api;

import cn.bdqfork.rpc.provider.LocalRegistry;
import cn.bdqfork.rpc.provider.Server;
import cn.bdqfork.rpc.provider.ServiceCenter;
import cn.bdqfork.rpc.provider.invoker.RemoteInvoker;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Main {
    public static void main(String[] args) {
        LocalRegistry localRegistry = new LocalRegistry();
        ServiceCenter server = new ServiceCenter("localhost", 8081, localRegistry);
        server.setInvoker(new RemoteInvoker(localRegistry));
        server.register("userService", new UserServiceImpl());
        server.start();
    }
}
