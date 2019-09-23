package cn.bdqfork.common.extension;

import java.lang.annotation.*;

/**
 * @author bdq
 * @since 2019/9/21
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Adaptive {
    String[] value() default {};
}
