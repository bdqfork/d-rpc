package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.registry.Registry;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;

/**
 * @author bdq
 * @date 2019-03-05
 */
public interface RpcBean extends ApplicationContextAware, InitializingBean, DisposableBean, BeanClassLoaderAware {
    Registry getOrCreateRegistry() throws ClassNotFoundException;
}
