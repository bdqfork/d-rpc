package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.compiler.AdaptiveCompiler;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.Protocol;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.RegistryFactory;
import cn.bdqfork.rpc.registry.util.RegistryUtils;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.common.URL;
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
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-03-03
 */
public class ServiceBean<T> implements InitializingBean, DisposableBean, ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
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
            URL url = buildUrl(protocolConfig, applicationConfig, service, serviceInterface);

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

    private URL buildUrl(ProtocolConfig protocolConfig, ApplicationConfig applicationConfig, Service service, Class<T> serviceInterface) {
        URL url = new URL(Const.PROTOCOL_REGISTRY, protocolConfig.getHost(), protocolConfig.getPort(), serviceInterface.getName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, service.group());
        url.addParameter(Const.VERSION_KEY, service.version());
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        url.addParameter(Const.INTERFACE_KEY, serviceInterface.getName());
        url.addParameter(Const.SERVER_KEY, protocolConfig.getServer());
        url.addParameter(Const.SERIALIZATION_KEY, protocolConfig.getSerialization());
        url.addParameter(Const.REGISTRY_KEY, buildRegistryUrlString());

        return url;
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


    private String buildRegistryUrlString() {
        List<RegistryConfig> registryConfigs = new ArrayList<>();
        if (service.registry().length > 0) {
            for (String registryConfigBeanName : service.registry()) {
                RegistryConfig registryConfig = applicationContext.getBean(registryConfigBeanName, RegistryConfig.class);
                registryConfigs.add(registryConfig);
            }
        } else {
            registryConfigs.addAll(applicationContext.getBeansOfType(RegistryConfig.class).values());
        }
        return registryConfigs.stream()
                .map(RegistryUtils::buildRegistryURL)
                .map(URL::buildString)
                .collect(Collectors.joining(","));
    }

    @Override
    public void destroy() throws Exception {
        exporters.forEach(Exporter::undoExport);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!inited) {
            inited = true;
            exporters = invokers.stream()
                    .map(protocol::export)
                    .collect(Collectors.toList());
        }
    }
}
