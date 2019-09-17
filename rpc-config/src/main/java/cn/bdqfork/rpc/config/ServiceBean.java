package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.RegistryProtocol;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.RegistryFactory;
import cn.bdqfork.rpc.registry.URL;
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
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    private RegistryFactory registryFactory = ExtensionLoader.getExtension(RegistryFactory.class);
    private ApplicationContext applicationContext;
    private Service service;
    private Class<T> serviceInterface;
    private List<Exporter> exporters;
    private boolean inited = false;

    @Override
    public void afterPropertiesSet() throws Exception {

        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);

        List<Invoker<?>> invokers = new ArrayList<>();

        List<Registry> registries = getRegistries();

        List<ProtocolConfig> protocolConfigs = getProtocolConfigs();

        RegistryProtocol registryProtocol = new RegistryProtocol(registries);

        for (ProtocolConfig protocolConfig : protocolConfigs) {
            URL url = buildUrl(protocolConfig, applicationConfig, service, serviceInterface);

            T proxy = applicationContext.getBean(serviceInterface);

            Invoker<?> invoker = registryProtocol.getInvoker(proxy, serviceInterface, url);

            invokers.add(invoker);

        }

        exporters = invokers.stream()
                .map(registryProtocol::export)
                .collect(Collectors.toList());

    }

    private void export() {
        exporters.forEach(Exporter::doExport);
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
        URL url = new URL(Const.PROTOCOL_PROVIDER, protocolConfig.getHost(), protocolConfig.getPort(), serviceInterface.getName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, service.group());
        url.addParameter(Const.VERSION_KEY, service.version());
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        url.addParameter(Const.INTERFACE_KEY, serviceInterface.getName());
        url.addParameter(Const.SERVER_KEY, protocolConfig.getServer());
        url.addParameter(Const.SERIALIZATION_KEY, protocolConfig.getSerialization());
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

    private List<Registry> getRegistries() {
        String ip = NetUtils.getIp();
        List<RegistryConfig> registryConfigs = getRegistryConfigs();
        return registryConfigs.stream()
                .map(registryConfig -> {
                    URL url = new URL(registryConfig.getProtocol(), ip, 0, "");
                    url.addParameter(Const.REGISTRY_KEY, registryConfig.getAddress());
                    url.addParameter(Const.SEESION_TIMEOUT_KEY, registryConfig.getSessionTimeout());
                    url.addParameter(Const.CONNECTION_TIMEOUT_KEY, registryConfig.getConnectionTimeout());
                    url.addParameter(Const.USERNAME_KEY, registryConfig.getUsername());
                    url.addParameter(Const.PASSWORD_KEY, registryConfig.getPassword());
                    return url;
                })
                .map(url -> registryFactory.getRegistry(url))
                .collect(Collectors.toList());
    }


    private List<RegistryConfig> getRegistryConfigs() {
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

    @Override
    public void destroy() throws Exception {
        exporters.forEach(Exporter::undoExport);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!inited) {
            inited = true;
            export();
        }
    }
}
