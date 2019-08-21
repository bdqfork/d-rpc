package cn.bdqfork.rpc.protocol;

import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.client.NettyClient;
import cn.bdqfork.rpc.protocol.netty.consumer.ClientContextHandler;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.protocol.serializer.JdkSerializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RemoteClientFactory;
import cn.bdqfork.rpc.remote.Serializer;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRemoteClientFactory implements RemoteClientFactory {

    @Override
    public RemoteClient createRemoteClient(String server, String serialization, String host, Integer port) {
        Serializer serializer;
        if ("jdk".equals(serialization)) {
            serializer = new JdkSerializer();
        } else {
            serializer = new HessianSerializer();
        }
        //序列化选择
        if ("netty".equals(server)) {
            ClientContextHandler handler = new ClientContextHandler();
            NettyInitializer.ChannelHandlerElement channelHandlerElement = new NettyInitializer.ChannelHandlerElement(handler);
            NettyInitializer nettyInitializer = new NettyInitializer(serializer, channelHandlerElement);
            return new NettyClient(host, port, nettyInitializer);
        }
        return null;
    }
}
