package cn.bdqfork.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    long timeout() default 5000;

    String group() default "";
    
}
