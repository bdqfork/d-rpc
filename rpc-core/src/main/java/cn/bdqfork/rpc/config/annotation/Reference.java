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

    String loadBalance() default "random";

    long timeout() default 1000;

    int retries() default 2;

    int connections() default 1;

    String[] registry() default {};

    String[] protocol() default {};

}
