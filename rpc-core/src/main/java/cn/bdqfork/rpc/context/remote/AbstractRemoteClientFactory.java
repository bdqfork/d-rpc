package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.URL;

/**
 * @author bdq
 * @since 2019/9/20
 */
public abstract class AbstractRemoteClientFactory implements RemoteClientFactory {
    private SerializerFactory serializerFactory = ExtensionLoader.getExtensionLoader(SerializerFactory.class)
            .getAdaptiveExtension();

    @Override
    public RemoteClient[] getRemoteClients(URL url) throws RpcException {
        String serialization = url.getParameter(Const.SERIALIZATION_KEY, "jdk");
        Serializer serializer = serializerFactory.getSerializer(serialization);
        if (serializer == null) {
            throw new RpcException("no serializer !");
        }

        int connections = Integer.parseInt(url.getParameter(Const.CONNECTIONS_KEY, "1"));

        RemoteClient[] remoteClients = new RemoteClient[connections];
        for (int i = 0; i < connections; i++) {
            remoteClients[i] = createRemoteClient(url, serializer);
        }
        return remoteClients;
    }

    protected abstract RemoteClient createRemoteClient(URL url, Serializer serializer) throws IllegalStateException;
}
