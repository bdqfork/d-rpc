package cn.bdqfork.rpc.config.context;

import cn.bdqfork.rpc.config.annotation.RpcComponentScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bdq
 * @since 2019-03-03
 */
public class RpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = resolvePackageToScan(importingClassMetadata);

        registerServiceAnnotationPostProcessor(registry, packagesToScan);

        registerReferenceAnnotationPostProcessor(registry);
    }

    private void registerServiceAnnotationPostProcessor(BeanDefinitionRegistry registry, Set<String> packagesToScan) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ServiceAnnotationPostProcessor.class)
                .addConstructorArgValue(packagesToScan)
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                .getBeanDefinition();
        registry.registerBeanDefinition(ServiceAnnotationPostProcessor.SERVICE_ANNOTATION_POST_PROCESSOR_NAME, beanDefinition);
    }

    private void registerReferenceAnnotationPostProcessor(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ReferenceAnnotationPostProcessor.class)
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                .getBeanDefinition();
        registry.registerBeanDefinition(ReferenceAnnotationPostProcessor.REFERENCE_ANNOTATION_POST_PROCESSOR_NAME, beanDefinition);
    }

    private Set<String> resolvePackageToScan(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(RpcComponentScan.class.getName()));
        Set<String> packagesToScan = new LinkedHashSet<>();
        String[] basePackages = attributes.getStringArray("basePackages");
        String[] value = attributes.getStringArray("value");
        packagesToScan.addAll(Arrays.asList(basePackages));
        packagesToScan.addAll(Arrays.asList(value));
        return packagesToScan;
    }

}
