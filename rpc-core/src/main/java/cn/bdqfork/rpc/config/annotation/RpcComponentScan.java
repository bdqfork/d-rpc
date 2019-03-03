package cn.bdqfork.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @author bdq
 * @date 2019-02-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcComponentScan {

    String[] value();

}
