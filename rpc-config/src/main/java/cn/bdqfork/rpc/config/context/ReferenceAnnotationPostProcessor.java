package cn.bdqfork.rpc.config.context;

import cn.bdqfork.rpc.config.ReferenceBean;
import cn.bdqfork.rpc.config.ReferenceInfo;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.netty.consumer.RpcInvoker;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import cn.bdqfork.rpc.proxy.ProxyType;
import cn.bdqfork.rpc.proxy.RpcProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceAnnotationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements
        BeanDefinitionRegistryPostProcessor {
    public static final String REFERENCE_ANNOTATION_POST_PROCESSOR_NAME = "referenceAnnotationPostProcessor";

    private List<ReferenceInfo> referenceInfos = new CopyOnWriteArrayList<>();

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        List<InjectionMetadata.InjectedElement> referenceFieldElements = new LinkedList<>();

        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();

        for (Field field : fields) {
            Reference reference = field.getAnnotation(Reference.class);
            if (reference == null) {
                continue;
            }

            Invoker<RpcResponse> invoker = new RpcInvoker(reference.timeout(), reference.retries());

            InjectionMetadata.InjectedElement referenceFieldElement = new ReferenceFieldElement(field, reference, invoker);
            referenceFieldElements.add(referenceFieldElement);

            ReferenceInfo referenceInfo = ReferenceInfo.build(reference, invoker);
            referenceInfos.add(referenceInfo);
        }
        if (referenceFieldElements.size() > 0) {
            InjectionMetadata injectionMetadata = new InjectionMetadata(beanClass, referenceFieldElements);

            try {
                injectionMetadata.inject(bean, beanName, pvs);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return pvs;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ReferenceBean.class)
                .addPropertyValue("referenceInfos", referenceInfos)
                .getBeanDefinition();
        registry.registerBeanDefinition(ReferenceBean.REFERENCE_BEAN_NAME, beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {
        private Field field;
        private Reference reference;
        private Invoker<RpcResponse> invoker;

        private ReferenceFieldElement(Field field, Reference reference, Invoker<RpcResponse> invoker) {
            super(field, null);
            this.field = field;
            this.reference = reference;
            this.invoker = invoker;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            RpcProxyFactoryBean rpcProxyFactoryBean = new RpcProxyFactoryBean.Builder()
                    .invoker(invoker)
                    .refName(reference.refName())
                    .serviceInterface(reference.serviceInterface())
                    .build();
            Object instance = rpcProxyFactoryBean.getProxy(ProxyType.JAVASSIST);
            field.setAccessible(true);
            field.set(target, instance);
        }
    }
}
