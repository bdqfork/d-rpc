package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.RegistryProtocol;
import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.proxy.ProxyFactory;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.RegistryFactory;
import cn.bdqfork.rpc.registry.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author bdq
 * @since 2019-03-04
 */
public class ReferenceBean<T> implements FactoryBean<Object>, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ReferenceBean.class);

    private ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class);
    private RegistryFactory registryFactory = ExtensionLoader.getExtension(RegistryFactory.class);
    private Reference reference;
    private Class<T> serviceInterface;
    private Invoker<T> invoker;
    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;

    public ReferenceBean(Reference reference) {
        this.reference = reference;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        getObject();
    }

    @Override
    public Object getObject() throws Exception {

        Registry registry = registryFactory.createRegistry(registryConfig);

        RegistryProtocol registryProtocol = new RegistryProtocol(registry);

        URL url = buildUrl(applicationConfig);

        invoker = registryProtocol.refer(serviceInterface, url);

        Exporter exporter = registryProtocol.export(invoker);

        exporter.doExport();

        return proxyFactory.getProxy(invoker);
    }

    private URL buildUrl(ApplicationConfig applicationConfig) {
        String host = NetUtils.getIp();//获得本机IP
        URL url = new URL(Const.PROTOCOL_CONSUMER, host, 0, serviceInterface.getName());
        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.VERSION_KEY, applicationConfig.getVersion());
        url.addParameter(Const.ENVIRONMENT_KEY, applicationConfig.getEnvironment());
        url.addParameter(Const.GROUP_KEY, reference.group());
        url.addParameter(Const.REF_NAME_KEY, reference.refName());
        url.addParameter(Const.INTERFACE_KEY, serviceInterface.getName());
        url.addParameter(Const.REF_NAME_KEY, reference.refName());
        url.addParameter(Const.RETRY_KEY, String.valueOf(reference.retries()));
        url.addParameter(Const.TIMEOUT_KEY, String.valueOf(reference.timeout()));
        url.addParameter(Const.CONNECTIONS_KEY, String.valueOf(reference.connections()));
        url.addParameter(Const.SIDE_KEY, Const.CONSUMER_SIDE);
        return url;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

}
