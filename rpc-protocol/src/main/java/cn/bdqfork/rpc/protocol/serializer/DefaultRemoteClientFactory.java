package cn.bdqfork.rpc.protocol.serializer;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.rpc.protocol.netty.NettyClient;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import cn.bdqfork.rpc.remote.Serializer;
import cn.bdqfork.rpc.remote.SerializerFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRemoteClientFactory implements RemoteClientFactory {
    private SerializerFactory serializerFactory = ExtensionLoader.getExtension(SerializerFactory.class);

    @Override
    public RemoteClient[] createRemoteClients(URL url) throws RpcException {
        String serialization = url.getParameter(Const.SERIALIZATION_KEY, "jdk");
        Serializer serializer = serializerFactory.getSerializer(serialization);
        if (serializer == null) {
            throw new RpcException("no serializer !");
        }

        int connections = Integer.parseInt(url.getParameter(Const.CONNECTIONS_KEY, "1"));

        RemoteClient[] remoteClients = new RemoteClient[connections];
        for (int i = 0; i < connections; i++) {
            remoteClients[i] = getRemoteClient(url, serializer);
        }
        return remoteClients;
    }

    private RemoteClient getRemoteClient(URL url, Serializer serializer) {
        String server = url.getParameter(Const.SERVER_KEY);
        if ("netty".equals(server)) {
            NettyClient nettyClient = new NettyClient(url.getHost(), url.getPort(), serializer);
            long timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY));
            nettyClient.setTimeout(timeout);
            return nettyClient;
        }
        return null;
    }
}
