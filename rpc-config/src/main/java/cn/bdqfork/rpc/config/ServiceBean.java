package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.RegistryProtocol;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.RegistryFactory;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.RpcServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bdq
 * @since 2019-03-03
 */
public class ServiceBean implements InitializingBean, ApplicationContextAware, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    public static final String SERVICE_BEAN = "serviceBean";
    private RegistryFactory registryFactory = ExtensionLoader.getExtension(RegistryFactory.class);
    private ApplicationContext applicationContext;
    private RpcServerFactory serverFactory = ExtensionLoader.getExtension(RpcServerFactory.class);
    private Registry registry;
    private RpcServer rpcServer;
    private List<Service> services;

    @Override
    public void destroy() throws Exception {

        log.info("closing server");

        registry.destroy();
        rpcServer.close();

        log.info("server closed");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {

        RegistryConfig registryConfig = applicationContext.getBean(RegistryConfig.class);

        registry = registryFactory.createRegistry(registryConfig);

        RegistryProtocol registryProtocol = new RegistryProtocol(registry);

        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);

        ProtocolConfig protocolConfig = applicationContext.getBean(ProtocolConfig.class);

        List<Invoker<?>> invokers = new ArrayList<>();

        services.forEach(service -> {
            URL url = buildUrl(protocolConfig, applicationConfig, service);
            Class type = service.serviceInterface();
            Object proxy = applicationContext.getBean(type);
            Invoker invoker = null;
            try {
                invoker = registryProtocol.getInvoker(proxy, type, url);
            } catch (RpcException e) {
                e.printStackTrace();
            }
            invokers.add(invoker);
        });

        rpcServer = serverFactory.createProviderServer(protocolConfig, invokers);

        log.info("starting server");
        rpcServer.start();
        log.info("server started");

        invokers.forEach(invoker -> {
            Exporter exporter = registryProtocol.export(invoker);
            exporter.doExport();
        });

    }

    private URL buildUrl(ProtocolConfig protocolConfig, ApplicationConfig applicationConfig, Service service) {
        URL url = new URL(Const.PROTOCOL_PROVIDER, protocolConfig.getHost(), protocolConfig.getPort(), service.serviceInterface().getName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, service.group());
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        url.addParameter(Const.REF_NAME_KEY, service.refName());
        url.addParameter(Const.INTERFACE_KEY, service.serviceInterface().getName());
        url.addParameter(Const.SERVER_KEY, protocolConfig.getServer());
        url.addParameter(Const.SERIALIZATION_KEY, protocolConfig.getSerialization());
        return url;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
