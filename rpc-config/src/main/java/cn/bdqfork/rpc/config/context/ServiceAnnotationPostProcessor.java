package cn.bdqfork.rpc.config.context;

import cn.bdqfork.common.util.ClassUtils;
import cn.bdqfork.rpc.config.ServiceBean;
import cn.bdqfork.rpc.config.annotation.Service;
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
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bdq
 * @since 2019-03-03
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

        Set<String> resolvedPackages = resolvePackages();

        if (resolvedPackages != null && resolvedPackages.size() > 0) {
            registerBean(registry, resolvedPackages);
        }

    }

    private void registerBean(BeanDefinitionRegistry registry, Set<String> resolvedPackages) {

        RpcClassPathBeanDefinitionScanner scanner = new RpcClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

        BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

        scanner.setBeanNameGenerator(beanNameGenerator);

        Set<BeanDefinitionHolder> beanDefinitionHolders = scanner.doScan(resolvedPackages.toArray(new String[]{}));

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {

            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

            String beanClassName = beanDefinition.getBeanClassName();

            Class<?> beanClass = getClass(beanClassName);

            Service service = beanClass.getAnnotation(Service.class);

            Class<?> interfaceClass = service.serviceInterface();

            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }

            String serviceBeanName = interfaceClass.getName();

            if (!interfaceClass.isInterface()) {
                throw new IllegalStateException("@Service interfaceClass = " + serviceBeanName + " is not interface");
            }

            AbstractBeanDefinition serviceBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ServiceBean.class)
                    .addPropertyValue("service", service)
                    .addPropertyValue("serviceInterface", interfaceClass)
                    .getBeanDefinition();

            if (scanner.checkCandidate(serviceBeanName, beanDefinition)) {
                registry.registerBeanDefinition(serviceBeanName, serviceBeanDefinition);
            } else {
                throw new IllegalStateException("Duplicate @Service interfaceClass = " + serviceBeanName);
            }

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

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
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
