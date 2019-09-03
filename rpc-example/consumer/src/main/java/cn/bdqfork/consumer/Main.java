package cn.bdqfork.consumer;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.consumer.client.UserServiceManager;
import cn.bdqfork.rpc.config.annotation.RpcComponentScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author bdq
 * @since 2019-02-15
 */
@ComponentScan
@RpcComponentScan(basePackages = "cn.bdqfork.consumer.client")
public class Main {
    public static void main(String[] args) throws RpcException {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);

        applicationContext.registerShutdownHook();

        UserServiceManager userServiceManager = applicationContext.getBean(UserServiceManager.class);

        while (true) {
            try {
                userServiceManager.sayHello();
                Thread.sleep(1000);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }
}
