package cn.bdqfork.provider;

import cn.bdqfork.provider.config.RpcConfigration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class Main {
    public static void main(String[] args) {
        AbstractApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcConfigration.class);
        applicationContext.registerShutdownHook();
    }
}
