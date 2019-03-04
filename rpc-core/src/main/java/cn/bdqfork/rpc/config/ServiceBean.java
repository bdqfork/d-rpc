package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import cn.bdqfork.rpc.provider.Exporter;
import cn.bdqfork.rpc.provider.RpcRemoteInvoker;
import cn.bdqfork.rpc.provider.server.NettyServer;
import cn.bdqfork.rpc.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @date 2019-03-03
 */
public class ServiceBean implements ApplicationContextAware, InitializingBean, DisposableBean, BeanClassLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    public static final String SERVICE_BEAN_NAME = "serviceBean";

    private Registry registry;

    private ApplicationContext context;

    private ClassLoader classLoader;

    private NettyServer nettyServer;

    private Exporter exporter;

    private List<ServiceConfig> serviceConfigs = new CopyOnWriteArrayList<>();

    public void setServiceConfigs(List<ServiceConfig> serviceConfigs) {
        this.serviceConfigs.addAll(serviceConfigs);
    }

    @Override
    public void destroy() throws Exception {

        log.info("closing server");

        registry.close();
        nettyServer.close();

        log.info("server closed");
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);

        String client = registryConfig.getClient();

        Class<?> clazz = Class.forName(client, false, classLoader);

        registry = (Registry) clazz.newInstance();

        registry.setRegistryConfig(registryConfig);

        registry.init();

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        exporter = new Exporter(protocolConfig.getHost(), protocolConfig.getPort(), registry);

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        serviceConfigs.forEach(serviceConfig -> exporter.export(applicationConfig.getApplicationName(), serviceConfig.getGroup(),
                serviceConfig.getServiceName(),
                serviceConfig.getRefName()));

        Invoker<RpcResponse> invoker = context.getBean(RpcRemoteInvoker.RPC_REMOTE_INVOKER_BEAN_NAME, RpcRemoteInvoker.class);

        nettyServer = new NettyServer(protocolConfig.getHost(), protocolConfig.getPort(), invoker);

        log.info("starting server");

        nettyServer.start();

        log.info("server started");

        serviceConfigs.clear();
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
