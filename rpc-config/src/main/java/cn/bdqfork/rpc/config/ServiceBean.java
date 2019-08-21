package cn.bdqfork.rpc.config;

import cn.bdqfork.common.extension.ExtensionUtils;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.exporter.ServiceExporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.remote.ProviderServer;
import cn.bdqfork.rpc.remote.ProviderServerFactory;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bdq
 * @date 2019-03-03
 */
public class ServiceBean extends AbstractRpcBean {
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    public static final String SERVICE_BEAN_NAME = "serviceBean";

    private ProviderServerFactory providerServerFactory = ExtensionUtils.getExtension(ProviderServerFactory.class);

    private Registry registry;

    private ProviderServer providerServer;

    private ServiceExporter exporter;

    private List<Service> services = new CopyOnWriteArrayList<>();

    public void setServices(List<Service> services) {
        this.services.addAll(services);
    }

    @Override
    public void destroy() throws Exception {

        log.info("closing server");

        registry.close();
        providerServer.close();

        log.info("server closed");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        registry = getOrCreateRegistry();

        ProtocolConfig protocolConfig = context.getBean(ProtocolConfig.class);

        exporter = new ServiceExporter(protocolConfig, registry);

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        services.forEach(service -> exporter.export(applicationConfig.getApplicationName(), service.group(),
                service.serviceInterface().getName(),
                service.refName()));

        Invoker<RpcResponse> invoker = context.getBean(RpcRemoteInvoker.RPC_REMOTE_INVOKER_BEAN_NAME, RpcRemoteInvoker.class);

        providerServer = providerServerFactory.createProviderServer(protocolConfig, invoker);

        log.info("starting server");

        providerServer.start();

        log.info("server started");

        services.clear();
    }

}
