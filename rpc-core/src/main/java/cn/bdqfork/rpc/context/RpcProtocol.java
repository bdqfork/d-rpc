package cn.bdqfork.rpc.context;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Protocol;
import cn.bdqfork.rpc.context.remote.*;
import cn.bdqfork.rpc.proxy.ProxyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/9/25
 */
public class RpcProtocol implements Protocol {
    private static final Map<String, RpcServer> rpcServerMap = new ConcurrentHashMap<>();

    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtensionLoader(RemoteClientFactory.class)
            .getAdaptiveExtension();

    private RpcServerFactory rpcServerFactory = ExtensionLoader.getExtensionLoader(RpcServerFactory.class)
            .getAdaptiveExtension();

    @Override
    public <T> Exporter export(Invoker<T> invoker) {
        URL url = invoker.getUrl();
        String side = url.getParameter(Const.SIDE_KEY);
        if (Const.PROVIDER_SIDE.equals(side)) {
            String key = url.getAddress();
            buildServerUrl(url);
            RpcServer rpcServer = rpcServerMap.get(key);
            if (rpcServer == null) {

                rpcServer = rpcServerFactory.getServer(buildServerUrl(url));

                rpcServer.start();

                rpcServerMap.put(key, rpcServer);
            }

            rpcServer.addInvoker(invoker);
        }
        return new RpcExporter(invoker);
    }

    private URL buildServerUrl(URL url) {
        String server = url.getParameter(Const.SERVER_KEY);
        String serialization = url.getParameter(Const.SERIALIZATION_KEY);
        URL serverUrl = new URL(server, url.getHost(), url.getPort(), "");
        serverUrl.addParameter(Const.SERIALIZATION_KEY, serialization);
        return serverUrl;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        RemoteClient[] remoteClients = remoteClientFactory.getRemoteClients(url);
        return new RpcInvoker<>(type, url, remoteClients);
    }

    @Override
    public void destory() {

    }
}
