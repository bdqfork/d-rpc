package cn.bdqfork.rpc.config.context;

import cn.bdqfork.rpc.config.ServiceBean;
import cn.bdqfork.rpc.config.ServiceBeanNameGenerator;
import cn.bdqfork.rpc.config.ServiceConfig;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.provider.RpcRemoteInvoker;
import cn.bdqfork.common.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bdq
 * @date 2019-03-03
 */
public class ServiceAnnotationPostProcessor implements BeanDefinitionRegistryPostProcessor,
        EnvironmentAware, ResourceLoaderAware, BeanClassLoaderAware {
    public static final String SERVICE_ANNOTATION_POST_PROCESSOR_NAME = "serviceAnnotationPostProcessor";
    private static final Logger log = LoggerFactory.getLogger(ServiceAnnotationPostProcessor.class);
    private Set<String> packagesToScan;
    private ClassLoader classLoader;
    private Environment environment;
    private ResourceLoader resourceLoader;

    public ServiceAnnotationPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        registerInvoker(registry);

        Set<String> resolvedPackages = resolvePackages();

        if (resolvedPackages!=null && resolvedPackages.size()>0){
            registerBean(registry, resolvedPackages);
        }

    }

    private void registerBean(BeanDefinitionRegistry registry, Set<String> resolvedPackages) {

        RpcClassPathBeanDefinitionScanner scanner = new RpcClassPathBeanDefinitionScanner(registry, environment, resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        BeanNameGenerator beanNameGenerator = new ServiceBeanNameGenerator(classLoader);
        scanner.setBeanNameGenerator(beanNameGenerator);

        Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(resolvedPackages.toArray(new String[]{}));

        List<ServiceConfig> serviceConfigs = new ArrayList<>();

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {

            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            String beanClassName = beanDefinition.getBeanClassName();
            Class<?> clazz = getClass(beanClassName);

            Service service = clazz.getAnnotation(Service.class);

            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setGroup(service.group());
            serviceConfig.setServiceName(service.serviceInterface().getName());
            serviceConfig.setRefName(service.refName());
            serviceConfigs.add(serviceConfig);

        }

        registerServiceBean(registry, serviceConfigs);
    }

    private void registerServiceBean(BeanDefinitionRegistry registry, List<ServiceConfig> serviceConfigs) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ServiceBean.class)
                .addPropertyValue("serviceConfigs", serviceConfigs)
                .getBeanDefinition();
        registry.registerBeanDefinition(ServiceBean.SERVICE_BEAN_NAME, beanDefinition);
    }

    private void registerInvoker(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(RpcRemoteInvoker.class)
                .getBeanDefinition();
        if (!registry.containsBeanDefinition(RpcRemoteInvoker.RPC_REMOTE_INVOKER_BEAN_NAME)) {
            registry.registerBeanDefinition(RpcRemoteInvoker.RPC_REMOTE_INVOKER_BEAN_NAME, beanDefinition);
        }
    }

    private Set<String> resolvePackages() {
        Set<String> resolvedPackages = new LinkedHashSet<>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            String resolvedPackage = environment.resolvePlaceholders(packageToScan.trim());
            resolvedPackages.add(resolvedPackage);
        }
        return resolvedPackages;
    }

    private Class<?> getClass(String beanClassName) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.getClass(beanClassName, classLoader);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return clazz;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
