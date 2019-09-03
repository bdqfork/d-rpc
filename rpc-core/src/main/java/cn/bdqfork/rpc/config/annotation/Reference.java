package cn.bdqfork.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2019-02-28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Reference {

    String group() default "rpc";

    Class<?> serviceInterface() default void.class;

    String refName() default "";

    long timeout() default 3000;

    int retries() default 0;

    int connections() default 1;

}
