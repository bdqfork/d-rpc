package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.consumer.RpcInvoker;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.consumer.exchanger.Exchanger;
import cn.bdqfork.rpc.registry.Registry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ReferenceBean implements ApplicationContextAware, BeanClassLoaderAware, InitializingBean, DisposableBean {
    public static final String REFERENCE_BEAN_NAME = "referenceBean";

    private ApplicationContext context;

    private ClassLoader classLoader;

    private Registry registry;

    private List<ReferenceConfig> referenceConfigs = new CopyOnWriteArrayList<>();

    public void setReferenceConfigs(List<ReferenceConfig> referenceConfigs) {
        this.referenceConfigs = referenceConfigs;
    }

    @Override
    public void destroy() throws Exception {

        registry.close();

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (referenceConfigs == null || referenceConfigs.size() == 0) {
            return;
        }

        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);

        String client = registryConfig.getClient();

        Class<?> clazz = Class.forName(client, false, classLoader);

        registry = (Registry) clazz.newInstance();

        registry.setRegistryConfig(registryConfig);

        registry.init();

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        Exchanger exchanger = new Exchanger(protocolConfig, registry);

        for (ReferenceConfig referenceConfig : referenceConfigs) {
            Reference reference = referenceConfig.getReference();
            exchanger.register(reference.group(), reference.serviceInterface().getName());
            exchanger.subscribe(reference.group(), reference.serviceInterface().getName());
            ClientPool clientPool = exchanger.getClientPool(reference.group(), reference.serviceInterface().getName());
            RpcInvoker invoker = (RpcInvoker) referenceConfig.getInvoker();
            invoker.setClientPool(clientPool);
        }

        referenceConfigs.clear();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
