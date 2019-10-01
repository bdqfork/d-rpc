package cn.bdqfork.rpc.proxy;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ProxyTest {

    @Test
    public void newProxyInstance() {
        try {
            UserService userService = (UserService) Proxy.newProxyInstance(Proxy.class.getClassLoader(), new Class[]{UserService.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("hello");
                    return null;
                }
            });
            userService.sayHello("");
        } catch (NotFoundException | CannotCompileException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}