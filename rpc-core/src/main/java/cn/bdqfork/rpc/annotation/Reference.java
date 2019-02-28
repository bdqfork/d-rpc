package cn.bdqfork.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-28
 */
@Autowired
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Reference {
    
    String group() default "";

    boolean required() default true;
}
