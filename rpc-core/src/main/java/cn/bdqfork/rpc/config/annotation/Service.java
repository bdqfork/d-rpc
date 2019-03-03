package cn.bdqfork.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    Class<?> serviceInterface();

    long timeout() default 5000;

    String group() default "rpc";

    String refName() default "";

}
