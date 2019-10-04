package cn.bdqfork.rpc.proxy;

/**
 * @author bdq
 * @since 2019/10/1
 */
public interface UserService {
    String getUsername(int id);

    void sayHello(String username);
}
