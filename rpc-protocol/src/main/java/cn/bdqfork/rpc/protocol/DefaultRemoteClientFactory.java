package cn.bdqfork.rpc.protocol;

import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.client.NettyClient;
import cn.bdqfork.rpc.protocol.netty.consumer.ClientContextHandler;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRemoteClientFactory implements RemoteClientFactory {

    @Override
    public RemoteClient createRemoteClient(String server, String serialization, String host, Integer port) {
        //序列化选择
        if ("netty".equals(server)) {
            ClientContextHandler handler = new ClientContextHandler();
            NettyInitializer.ChannelHandlerElement channelHandlerElement = new NettyInitializer.ChannelHandlerElement(handler);
            NettyInitializer nettyInitializer = new NettyInitializer(new HessianSerializer(), channelHandlerElement);
            return new NettyClient(host, port, nettyInitializer);
        }
        return null;
    }
}
