package cn.bdqfork.common.extension;

import java.lang.annotation.*;

/**
 * 注解在集合扩展接口实现类上，表示激活扩展
 *
 * @author bdq
 * @since 2019/9/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Activate {
    /**
     * url必须包含的key类型
     */
    String[] value() default {};

    /**
     * 分组
     */
    String[] group() default {};

    /**
     * 扩展调用顺序，order越小，越早执行
     */
    int order() default 0;
}
