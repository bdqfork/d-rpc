package cn.bdqfork.consuemr.client;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.config.annotation.Reference;
import org.springframework.stereotype.Component;

/**
 * @author bdq
 * @date 2019-03-04
 */
@Component
public class UserServiceManager {
    @Reference(group = "rpc-test", serviceInterface = UserService.class, retries = 3)
    private UserService userService;

    public void sayHello() {
        String username = userService.getUserName();
        userService.sayHello(username);
    }


}
