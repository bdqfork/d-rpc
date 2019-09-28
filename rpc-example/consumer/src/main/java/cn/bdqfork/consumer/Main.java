package cn.bdqfork.consumer;

import cn.bdqfork.consumer.client.UserServiceManager;
import cn.bdqfork.rpc.config.annotation.RpcComponentScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.CountDownLatch;

/**
 * @author bdq
 * @since 2019-02-15
 */
@ComponentScan
@RpcComponentScan(basePackages = "cn.bdqfork.consumer.client")
public class Main {
    public static void main(String[] args) throws InterruptedException {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);

        applicationContext.registerShutdownHook();

        CountDownLatch latch=new CountDownLatch(1);

        UserServiceManager userServiceManager = applicationContext.getBean(UserServiceManager.class);

        userServiceManager.sayHello();
        latch.await();
        //System.exit(0);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
