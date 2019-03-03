package cn.bdqfork.provider.api;

import cn.bdqfork.rpc.config.annotation.Service;

/**
 * @author bdq
 * @date 2019-02-15
 */
@Service(serviceInterface = UserService.class, group = "rpc-test", refName = "userService")
public class UserServiceImpl implements UserService {

    @Override
    public String getUserName() {
        return "test";
    }

    @Override
    public void sayHello(String userName) {
        System.out.println(String.format("say hello %s !", userName));
    }
}
