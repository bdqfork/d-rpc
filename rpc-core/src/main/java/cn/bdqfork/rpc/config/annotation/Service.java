package cn.bdqfork.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2019-02-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String group() default "rpc";

    Class<?> serviceInterface() default void.class;

    long timeout() default 1000;

    int retries() default 2;

    String refName() default "";

}
