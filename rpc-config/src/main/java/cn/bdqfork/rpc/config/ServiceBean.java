package cn.bdqfork.rpc.config;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.config.ApplicationConfig;
import cn.bdqfork.common.config.ProtocolConfig;
import cn.bdqfork.common.config.RegistryConfig;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.extension.compiler.AdaptiveCompiler;
import cn.bdqfork.common.util.RegistryUtils;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.protocol.Protocol;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-03-03
 */
public class ServiceBean<T> implements InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    private Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
    private ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
    private ApplicationContext applicationContext;
    private Service service;
    private Class<T> serviceInterface;
    private List<Invoker<?>> invokers;
    private List<Exporter> exporters;
    private boolean inited = false;

    @Override
    public void afterPropertiesSet() throws Exception {

        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);

        AdaptiveCompiler.setDefaultCompiler(applicationConfig.getCompiler());

        invokers = new ArrayList<>();

        List<ProtocolConfig> protocolConfigs = getProtocolConfigs();

        for (ProtocolConfig protocolConfig : protocolConfigs) {
            URL url = buildUrl(protocolConfig, applicationConfig, service);
            T proxy = applicationContext.getBean(serviceInterface);

            Invoker<?> invoker = proxyFactory.getInvoker(proxy, serviceInterface, url);

            invokers.add(invoker);

        }

    }

    private List<ProtocolConfig> getProtocolConfigs() {
        String[] protocols = service.protocol();
        List<ProtocolConfig> protocolConfigs = new ArrayList<>();
        if (protocols.length > 0) {
            for (String protocol : protocols) {
                ProtocolConfig protocolConfig = applicationContext.getBean(protocol, ProtocolConfig.class);
                protocolConfigs.add(protocolConfig);
            }
        } else {
            protocolConfigs.addAll(applicationContext.getBeansOfType(ProtocolConfig.class).values());
        }
        return protocolConfigs;
    }

    private URL buildUrl(ProtocolConfig protocolConfig, ApplicationConfig applicationConfig, Service service) {
        URL url = new URL(Const.PROTOCOL_REGISTRY, protocolConfig.getHost(), protocolConfig.getPort(), getServiceName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, service.group());
        url.addParameter(Const.VERSION_KEY, service.version());
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER);
        url.addParameter(Const.INTERFACE_KEY, getServiceName());
        url.addParameter(Const.SERVER_KEY, protocolConfig.getServer());
        url.addParameter(Const.SERIALIZATION_KEY, protocolConfig.getSerialization());
        url.addParameter(Const.ACCESS_LOG_KEY, service.accesslog());
        
        List<RegistryConfig> registryConfigs = getRegistryConfigs(service);
        url.addParameter(Const.REGISTRY_KEY, RegistryUtils.buildRegistryUrlString(registryConfigs));

        return url;
    }

    private String getServiceName() {
        return serviceInterface.getName();
    }

    private List<RegistryConfig> getRegistryConfigs(Service service) {
        List<RegistryConfig> registryConfigs = new ArrayList<>();
        if (service.registry().length > 0) {
            for (String registryConfigBeanName : service.registry()) {
                RegistryConfig registryConfig = applicationContext.getBean(registryConfigBeanName, RegistryConfig.class);
                registryConfigs.add(registryConfig);
            }
        } else {
            registryConfigs.addAll(applicationContext.getBeansOfType(RegistryConfig.class).values());
        }
        return registryConfigs;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void destroy() throws Exception {
        log.info("Destroy service {} !", getServiceName());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!inited) {
            inited = true;
            invokers.forEach(protocol::export);
        }
    }
}
