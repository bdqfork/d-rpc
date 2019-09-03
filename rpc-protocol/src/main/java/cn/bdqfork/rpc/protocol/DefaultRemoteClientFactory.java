package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.client.NettyClient;
import cn.bdqfork.rpc.protocol.netty.consumer.ClientContextHandler;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.protocol.serializer.JdkSerializer;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import cn.bdqfork.rpc.remote.Serializer;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRemoteClientFactory implements RemoteClientFactory {

    @Override
    public RemoteClient createRemoteClient(URL url) throws RpcException {
        String serialization = url.getParameter(Const.SERIALIZATION_KEY, "jdk");
        Serializer serializer = getSerializer(serialization);
        if (serializer == null) {
            throw new RpcException("no serializer !");
        }
        return getRemoteClient(url, serializer);
    }

    private Serializer getSerializer(String serialization) {
        if ("jdk".equals(serialization)) {
            return new JdkSerializer();
        }
        if ("hessian".equals(serialization)) {
            return new HessianSerializer();
        }
        return null;
    }

    private RemoteClient getRemoteClient(URL url, Serializer serializer) {
        String server = url.getParameter(Const.SERVER_KEY);
        if ("netty".equals(server)) {
            ClientContextHandler handler = new ClientContextHandler();
            NettyInitializer.ChannelHandlerElement channelHandlerElement = new NettyInitializer.ChannelHandlerElement(handler);
            NettyInitializer nettyInitializer = new NettyInitializer(serializer, channelHandlerElement);
            return new NettyClient(url.getHost(), url.getPort(), nettyInitializer);
        }
        return null;
    }
}
