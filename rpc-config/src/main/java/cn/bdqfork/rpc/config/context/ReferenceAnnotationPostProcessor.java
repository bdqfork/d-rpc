package cn.bdqfork.rpc.config.context;

import cn.bdqfork.rpc.config.ApplicationConfig;
import cn.bdqfork.rpc.config.ReferenceBean;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.rpc.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceAnnotationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements
        MergedBeanDefinitionPostProcessor, ApplicationContextAware, BeanClassLoaderAware {
    public static final String REFERENCE_ANNOTATION_POST_PROCESSOR_NAME = "referenceAnnotationPostProcessor";
    private final transient Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);
    private Map<String, RegistryConfig> registryConfigs;
    private ApplicationContext applicationContext;
    private ClassLoader classLoader;

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        Class<?> beanType = bean.getClass();
        InjectionMetadata metadata = findReferenceMetadata(beanType, beanName, pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @Reference"
                    + " dependencies is failed", ex);
        }
        return pvs;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata injectionMetadata = findReferenceMetadata(beanType, beanName, null);
            injectionMetadata.checkConfigMembers(beanDefinition);
        }
    }

    private InjectionMetadata findReferenceMetadata(Class<?> beanType, String beanName, PropertyValues pvs) {
        InjectionMetadata metadata = injectionMetadataCache.get(beanName);
        if (InjectionMetadata.needsRefresh(metadata, beanType)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(beanName);
                if (InjectionMetadata.needsRefresh(metadata, beanType)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildReferenceMetadata(beanType);
                    this.injectionMetadataCache.put(beanName, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildReferenceMetadata(Class<?> beanType) {
        List<InjectionMetadata.InjectedElement> annotationElements = new LinkedList<>();
        ReflectionUtils.doWithLocalFields(beanType, field -> {
            Reference reference = field.getAnnotation(Reference.class);
            if (reference != null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@Reference annotation is not supported on static fields");
                }
                ReferenceFieldElementReference referenceFieldElement = new ReferenceFieldElementReference(field, reference);
                annotationElements.add(referenceFieldElement);
            }
        });
        ReflectionUtils.doWithLocalMethods(beanType, method -> {
            Reference reference = method.getAnnotation(Reference.class);
            if (reference != null) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException("@Reference annotation is not supported on static methods");
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalStateException("@Reference annotation requires a single-arg method: " + method);
                }
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method, beanType);
                ReferenceMethodElementReference referenceMethodElement = new ReferenceMethodElementReference(method, pd, reference);
                annotationElements.add(referenceMethodElement);
            }
        });
        return new InjectionMetadata(beanType, annotationElements);
    }

    private Object createProxy(Class<?> serviceInterface, ReferenceBean referenceBean) throws Exception {
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(referenceBean);
        handler.init();
        return Proxy.newProxyInstance(classLoader, new Class[]{serviceInterface}, handler);
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private class ReferenceInvocationHandler implements InvocationHandler {
        private ReferenceBean referenceBean;
        private Object bean;

        public ReferenceInvocationHandler(ReferenceBean referenceBean) {
            this.referenceBean = referenceBean;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (bean == null) {
                init();
            }
            return method.invoke(bean, args);
        }

        private void init() throws Exception {
            bean = referenceBean.getObject();
        }
    }

    private class ReferenceMethodElementReference extends InjectionMetadata.InjectedElement {
        private Object bean;
        private Reference reference;

        private ReferenceMethodElementReference(Member member, PropertyDescriptor pd, Reference reference) {
            super(member, pd);
            this.reference = reference;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Method method = (Method) member;
            ReferenceBean referenceBean = buildReferenceBean(method.getParameterTypes()[0], reference);
            bean = createProxy(method.getParameterTypes()[0], referenceBean);
            ReflectionUtils.makeAccessible(method);
            method.invoke(target, bean);
        }
    }

    private class ReferenceFieldElementReference extends InjectionMetadata.InjectedElement {
        private Object bean;
        private Reference reference;

        protected ReferenceFieldElementReference(Member member, Reference reference) {
            super(member, null);
            this.reference = reference;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) member;
            ReferenceBean referenceBean = buildReferenceBean(field.getType(), reference);
            bean = createProxy(field.getType(), referenceBean);
            ReflectionUtils.makeAccessible(field);
            field.set(target, bean);
        }

    }

    @SuppressWarnings("unchecked")
    private <T> ReferenceBean<T> buildReferenceBean(Class<T> serviceInterface, Reference reference) throws Exception {
        ReferenceBean<T> referenceBean = new ReferenceBean<>(reference);
        if (void.class == reference.serviceInterface()) {
            referenceBean.setServiceInterface(serviceInterface);
        } else {
            referenceBean.setServiceInterface((Class<T>) reference.serviceInterface());
        }
        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);
        List<RegistryConfig> registryConfigs = getRegistryConfigs(reference);
        referenceBean.setApplicationConfig(applicationConfig);
        referenceBean.setRegistryConfigs(registryConfigs);
        referenceBean.afterPropertiesSet();
        return referenceBean;
    }

    private List<RegistryConfig> getRegistryConfigs(Reference reference) {
        if (registryConfigs == null) {
            this.registryConfigs = applicationContext.getBeansOfType(RegistryConfig.class);
        }
        List<RegistryConfig> registryConfigs = new ArrayList<>();
        if (reference.registry().length > 0) {
            for (String registryConfigBeanName : reference.registry()) {
                RegistryConfig registryConfig = this.registryConfigs.get(registryConfigBeanName);
                registryConfigs.add(registryConfig);
            }
        } else {
            registryConfigs.addAll(this.registryConfigs.values());
        }
        return registryConfigs;
    }

}
