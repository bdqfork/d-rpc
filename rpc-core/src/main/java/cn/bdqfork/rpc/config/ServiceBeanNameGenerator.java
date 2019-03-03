package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.common.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ServiceBeanNameGenerator implements BeanNameGenerator {
    private static final Logger log = LoggerFactory.getLogger(ServiceBeanNameGenerator.class);
    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    private ClassLoader classLoader;

    public ServiceBeanNameGenerator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.getClass(definition.getBeanClassName(), classLoader);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        Service service = clazz.getAnnotation(Service.class);
        if (!StringUtils.isEmpty(service)) {
            return service.refName();
        }
        return beanNameGenerator.generateBeanName(definition, registry);
    }

}
