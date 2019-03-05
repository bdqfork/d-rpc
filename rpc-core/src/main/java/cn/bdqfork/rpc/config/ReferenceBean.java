package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.consumer.RpcInvoker;
import cn.bdqfork.rpc.consumer.client.ClientPool;
import cn.bdqfork.rpc.consumer.exchanger.Exchanger;
import cn.bdqfork.rpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ReferenceBean implements ApplicationContextAware, BeanClassLoaderAware, InitializingBean, DisposableBean {
    public static final String REFERENCE_BEAN_NAME = "referenceBean";

    private static final Logger log = LoggerFactory.getLogger(ReferenceBean.class);

    private ApplicationContext context;

    private ClassLoader classLoader;

    private Registry registry;

    private ReferenceConfigCallback referenceConfigCallback;

    @Override
    public void destroy() throws Exception {

        log.info("closing ......");

        registry.close();

        log.info("closed");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        List<ReferenceConfig> referenceConfigs = referenceConfigCallback.getReferenceConfigs();

        if (referenceConfigs == null || referenceConfigs.size() == 0) {
            return;
        }

        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);

        String client = registryConfig.getClient();

        Class<?> clazz = Class.forName(client, false, classLoader);
        Constructor<?> constructor = clazz.getConstructor(RegistryConfig.class);

        registry = (Registry) constructor.newInstance(registryConfig);

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        Exchanger exchanger = new Exchanger(protocolConfig, registry);

        for (ReferenceConfig referenceConfig : referenceConfigs) {

            Reference reference = referenceConfig.getReference();

            //注册消费者，以及订阅提供者
            exchanger.register(reference.group(), reference.serviceInterface().getName());
            exchanger.subscribe(reference.group(), reference.serviceInterface().getName());

            //设置连接池
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

    public void setReferenceConfigCallback(ReferenceConfigCallback referenceConfigCallback) {
        this.referenceConfigCallback = referenceConfigCallback;
    }

    public interface ReferenceConfigCallback {

        /**
         * 回调获取ReferenceConfig信息
         *
         * @return
         */
        List<ReferenceConfig> getReferenceConfigs();

    }
}
