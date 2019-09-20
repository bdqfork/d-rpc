package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.context.RegistryProtocol;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.RegistryFactory;
import cn.bdqfork.rpc.util.RegistryUtils;
import cn.bdqfork.rpc.URL;
import cn.bdqfork.rpc.Invoker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceBean<T> implements FactoryBean<Object>, InitializingBean {
    private RegistryFactory registryFactory = ExtensionLoader.getExtension(RegistryFactory.class);
    private ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class);
    private Reference reference;
    private Class<T> serviceInterface;
    private ApplicationConfig applicationConfig;
    private List<RegistryConfig> registryConfigs;
    private T bean;

    public ReferenceBean(Reference reference) {
        this.reference = reference;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (bean == null && !reference.isLazy()) {
            getObject();
        }
    }

    @Override
    public Object getObject() throws Exception {
        if (bean != null) {
            return bean;
        }
        List<Registry> registries = getRegistries();
        RegistryProtocol registryProtocol = new RegistryProtocol(registries);

        URL url = buildUrl(applicationConfig);

        Invoker<T> invoker = registryProtocol.refer(serviceInterface, url);

        Exporter exporter = registryProtocol.export(invoker);

        exporter.doExport();

        bean = proxyFactory.getProxy(invoker);

        return bean;
    }

    private URL buildUrl(ApplicationConfig applicationConfig) {
        String host = NetUtils.getIp();//获得本机IP
        URL url = new URL(Const.PROTOCOL_CONSUMER, host, 0, serviceInterface.getName());
        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());

        String version = reference.version();
        if (StringUtils.isNotBlank(version)) {
            url.addParameter(Const.VERSION_KEY, version);
        } else {
            url.addParameter(Const.VERSION_KEY, applicationConfig.getVersion());
        }

        url.addParameter(Const.ENVIRONMENT_KEY, applicationConfig.getEnvironment());
        url.addParameter(Const.GROUP_KEY, reference.group());
        url.addParameter(Const.INTERFACE_KEY, serviceInterface.getName());
        url.addParameter(Const.RETRY_KEY, String.valueOf(reference.retries()));
        url.addParameter(Const.TIMEOUT_KEY, String.valueOf(reference.timeout()));
        url.addParameter(Const.CONNECTIONS_KEY, String.valueOf(reference.connections()));
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        url.addParameter(Const.LOADBALANCE_KEY, reference.loadBalance());
        url.addParameter(Const.ASYNC_KEY,reference.async());
        return url;
    }

    private List<Registry> getRegistries() {
        return registryConfigs.stream()
                .map(RegistryUtils::buildRegistryURL)
                .map(url -> registryFactory.getRegistry(url))
                .collect(Collectors.toList());
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
        this.registryConfigs = registryConfigs;
    }

}
