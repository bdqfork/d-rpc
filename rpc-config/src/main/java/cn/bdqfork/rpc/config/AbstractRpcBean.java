package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.registry.Registry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author bdq
 * @date 2019-03-05
 */
public abstract class AbstractRpcBean implements RpcBean {
    protected ApplicationContext context;

    protected ClassLoader classLoader;

    @Override
    public Registry getOrCreateRegistry() throws ClassNotFoundException {
        try {
            return context.getBean(Registry.class);
        } catch (NoSuchBeanDefinitionException e) {
            createRegistry();
            return context.getBean(Registry.class);
        }
    }

    private void createRegistry() throws ClassNotFoundException {
        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);
        String client = registryConfig.getClient();

        Class<?> clazz = Class.forName(client, false, classLoader);

        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(Registry.class)
                .setAbstract(true)
                .getBeanDefinition();

        defaultListableBeanFactory.registerBeanDefinition(Registry.REGISTRY_NAME, beanDefinition);

        beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clazz)
                .addPropertyValue("registryConfig", registryConfig)
                .getBeanDefinition();

        defaultListableBeanFactory.registerBeanDefinition(Registry.REGISTRY_NAME, beanDefinition);
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
