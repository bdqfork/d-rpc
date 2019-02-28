package cn.bdqfork.provider.api;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class UserServiceImpl implements UserService {
    @Override
    public String getUserName() {
        return "test";
    }

    @Override
    public void sayHello(String userName) {
        System.out.println(String.format("hello %s !", userName));
    }
}
