package cn.bdqfork.rpc.config;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.config.ApplicationConfig;
import cn.bdqfork.common.config.RegistryConfig;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.common.util.RegistryUtils;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Protocol;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceBean<T> implements FactoryBean<Object>, InitializingBean {
    private ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class)
            .getAdaptiveExtension();
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
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

        URL url = buildUrl(applicationConfig);

        Invoker<T> invoker = protocol.refer(serviceInterface, url);

        protocol.export(invoker);

        bean = proxyFactory.getProxy(invoker);

        return bean;
    }

    private URL buildUrl(ApplicationConfig applicationConfig) {
        //获得本机IP
        URL url = new URL(Const.PROTOCOL_REGISTRY, NetUtils.getIp(), 0, getServiceName());
        url.addParameter(Const.INTERFACE_KEY, getServiceName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.VERSION_KEY, getVersion(applicationConfig));
        url.addParameter(Const.ENVIRONMENT_KEY, applicationConfig.getEnvironment());
        url.addParameter(Const.PROXY_KEY, reference.proxy());
        url.addParameter(Const.GROUP_KEY, reference.group());
        url.addParameter(Const.RETRY_KEY, String.valueOf(reference.retries()));
        url.addParameter(Const.TIMEOUT_KEY, String.valueOf(reference.timeout()));
        url.addParameter(Const.CONNECTIONS_KEY, String.valueOf(reference.connections()));
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        url.addParameter(Const.CLUSTER_KEY, reference.cluster());
        url.addParameter(Const.LOADBALANCE_KEY, reference.loadBalance());
        url.addParameter(Const.ASYNC_KEY, reference.async());

        url.addParameter(Const.SERVER_KEY, reference.protocol());
        url.addParameter(Const.REGISTRY_KEY, RegistryUtils.buildRegistryUrlString(registryConfigs));
        return url;
    }

    private String getServiceName() {
        return serviceInterface.getName();
    }

    private String getVersion(ApplicationConfig applicationConfig) {
        String version = reference.version();
        if (StringUtils.isBlank(version)) {
            version = applicationConfig.getVersion();
        }
        return version;
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
