package cn.bdqfork.rpc.proxy;

/**
 * @author bdq
 * @since 2019/10/4
 */
public class UserServiceImpl implements UserService {
    @Override
    public String getUsername(int id) {
        return "bob";
    }

    @Override
    public void sayHello(String username) {
        System.out.println("hello " + username);
    }
}
