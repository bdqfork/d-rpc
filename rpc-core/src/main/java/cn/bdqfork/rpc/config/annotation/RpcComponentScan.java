package cn.bdqfork.rpc.config.annotation;

import cn.bdqfork.rpc.config.context.RpcComponentScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcComponentScanRegistrar.class)
public @interface RpcComponentScan {

    String[] value() default {};

    String[] basePackages() default {};

}
