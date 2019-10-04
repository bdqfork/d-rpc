package cn.bdqfork.rpc.proxy;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyTest {

    @Test
    public void newProxyInstance() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        UserService userService = (UserService) Proxy.newProxyInstance(Proxy.class.getClassLoader(), new Class[]{UserService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("proxy");
                return method.invoke(userServiceImpl, args);
            }
        });
        userService.sayHello(userService.getUsername(0));
    }
}