package cn.bdqfork.provider;

import cn.bdqfork.provider.api.UserService;
import cn.bdqfork.provider.config.RpcConfigration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcConfigration.class);
        UserService userService = applicationContext.getBean(UserService.class);
        userService.sayHello("test");
    }
}
