package cn.bdqfork.provider.impl;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.rpc.config.annotation.Service;

/**
 * @author bdq
 * @date 2019-02-15
 */
@Service(group = "rpc-test", version = "1", accesslog = true)
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
