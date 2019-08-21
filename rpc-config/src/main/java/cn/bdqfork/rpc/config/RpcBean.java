package cn.bdqfork.rpc.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;

/**
 * @author bdq
 * @since 2019-03-05
 */
public interface RpcBean extends ApplicationContextAware, InitializingBean, DisposableBean, BeanClassLoaderAware {
}
