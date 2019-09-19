package cn.bdqfork.consumer.client;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.remote.context.RpcContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2019-03-04
 */
@Component
public class UserServiceManager {
    @Reference(group = "rpc-test", version = "1", async = true)
    private UserService userService;

    public void sayHello() {
        userService.getUserName();
        try {
            String username = (String) RpcContext.getRpcContext()
                    .getFuture()
                    .get();
            userService.sayHello(username);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


}
