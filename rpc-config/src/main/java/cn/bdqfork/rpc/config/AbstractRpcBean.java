package cn.bdqfork.rpc.config;

import cn.bdqfork.common.extension.ExtensionUtils;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.security.cert.Extension;

/**
 * @author bdq
 * @date 2019-03-05
 */
public abstract class AbstractRpcBean implements RpcBean {
    protected ApplicationContext context;

    protected ClassLoader classLoader;

    private Registry registry;

    protected Registry getOrCreateRegistry() {
        try {
            registry = context.getBean(Registry.class);
        } catch (NoSuchBeanDefinitionException e) {
            registry = createRegistry();
        }
        return registry;
    }

    private Registry createRegistry() {
        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);
        RegistryFactory registryFactory = ExtensionUtils.getExtension(RegistryFactory.class);
        return registryFactory.createRegistry(registryConfig);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
