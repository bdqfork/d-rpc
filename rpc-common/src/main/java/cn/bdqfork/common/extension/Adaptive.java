package cn.bdqfork.common.extension;

import java.lang.annotation.*;

/**
 * 注解在扩展实现类或实现类的方法上，只能有一个实现类可以使用该注解，表示默认使用该扩展实现类
 *
 * @author bdq
 * @since 2019/9/21
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Adaptive {
    /**
     * url中的key，根据url中的key的值来选择具体的实现类，若无，则使用默认扩展
     */
    String[] value() default {};
}
