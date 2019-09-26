package cn.bdqfork.provider;

import cn.bdqfork.rpc.config.annotation.RpcComponentScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author bdq
 * @since 2019-02-22
 */
@ComponentScan
@RpcComponentScan(basePackages = "cn.bdqfork.provider.impl")
public class Main {
    public static void main(String[] args) {
        AbstractApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);
        applicationContext.registerShutdownHook();
//        System.exit(0);
    }
}
