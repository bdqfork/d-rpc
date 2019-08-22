package cn.bdqfork.rpc.config;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionUtils;
import cn.bdqfork.rpc.config.annotation.Service;
import cn.bdqfork.rpc.exporter.ServiceExporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;
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
 * @since 2019-03-03
 */
public class ServiceBean extends AbstractRpcBean {
    private static final Logger log = LoggerFactory.getLogger(ServiceBean.class);
    public static final String SERVICE_BEAN_NAME = "serviceBean";

    private ProviderServerFactory providerServerFactory = ExtensionUtils.getExtension(ProviderServerFactory.class);

    private Registry registry;

    private ProviderServer providerServer;

    private ServiceExporter exporter;

    private List<Service> services = new CopyOnWriteArrayList<>();

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

        exporter = new ServiceExporter(registry);

        ApplicationConfig applicationConfig = context.getBean(ApplicationConfig.class);

        services.stream()
                .map(service -> buildUrl(protocolConfig, applicationConfig, service))
                .forEach(url -> exporter.export(url));

        Invoker<RpcResponse> invoker = context.getBean(RpcRemoteInvoker.RPC_REMOTE_INVOKER_BEAN_NAME, RpcRemoteInvoker.class);

        providerServer = providerServerFactory.createProviderServer(protocolConfig, invoker);

        log.info("starting server");

        providerServer.start();

        log.info("server started");

        services.clear();
    }

    private URL buildUrl(ProtocolConfig protocolConfig, ApplicationConfig applicationConfig, Service service) {
        URL url = new URL(Const.PROTOCOL_PROVIDER, protocolConfig.getHost(), protocolConfig.getPort(), service.serviceInterface().getName());

        url.addParameter(Const.APPLICATION_KEY, applicationConfig.getApplicationName());
        url.addParameter(Const.GROUP_KEY, service.group());
        url.addParameter(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        url.addParameter(Const.REF_NAME_KEY, service.refName());
        url.addParameter(Const.SERVER_KEY, protocolConfig.getServer());
        url.addParameter(Const.SERIALIZATION_KEY, protocolConfig.getSerialization());
        url.addParameter(Const.TIMEOUT_KEY, String.valueOf(service.timeout()));
        return url;
    }

    public void setServices(List<Service> services) {
        this.services.addAll(services);
    }

}
