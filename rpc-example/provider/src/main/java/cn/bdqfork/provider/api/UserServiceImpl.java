package cn.bdqfork.provider.api;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class UserServiceImpl implements UserService {
    private int port;

    public UserServiceImpl(int port) {
        this.port = port;
    }

    @Override
    public String getUserName() {
        return "test";
    }

    @Override
    public void sayHello(String userName) {
        System.out.println(String.format("port:%d say hello %s !", port, userName));
    }
}
