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

    String version() default "";

    Class<?> serviceInterface() default void.class;

    String proxy() default "jdk";

    String cluster() default "failover";

    String loadBalance() default "random";

    long timeout() default 1000;

    int retries() default 2;

    int connections() default 1;

    String[] registry() default {};

    /**
     * 支持的协议,多个协议用,隔开
     */
    String protocol() default "";

    boolean isLazy() default false;

    boolean async() default false;

}
