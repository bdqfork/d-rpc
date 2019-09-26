package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.protocol.AbstractProtocol;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.protocol.*;

/**
 * @author bdq
 * @since 2019/9/25
 */
public class RpcProtocol extends AbstractProtocol {
    private RemoteClientFactory remoteClientFactory = ExtensionLoader.getExtensionLoader(RemoteClientFactory.class)
            .getAdaptiveExtension();
    private RpcServerFactory rpcServerFactory = ExtensionLoader.getExtensionLoader(RpcServerFactory.class)
            .getAdaptiveExtension();

    @Override
    protected <T> void doExport(Invoker<T> invoker) {
        URL url = invoker.getUrl();
        String key = url.getAddress();
        RpcServer rpcServer = rpcServerMap.get(key);
        if (rpcServer == null) {

            URL serverUrl = buildServerUrl(url);
            rpcServer = rpcServerFactory.getServer(serverUrl);

            rpcServer.start();

            rpcServerMap.put(key, rpcServer);
        }

        rpcServer.addInvoker(invoker);
    }

    private URL buildServerUrl(URL url) {
        URL serverUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
        String serialization = url.getParameter(Const.SERIALIZATION_KEY);
        serverUrl.addParameter(Const.SERIALIZATION_KEY, serialization);
        return serverUrl;
    }

    @Override
    protected <T> Invoker<T> getBindInvoker(Class<T> type, URL url) {
        RemoteClient[] remoteClients = remoteClientFactory.getRemoteClients(url);
        return new RpcInvoker<>(type, url, remoteClients);
    }
}
