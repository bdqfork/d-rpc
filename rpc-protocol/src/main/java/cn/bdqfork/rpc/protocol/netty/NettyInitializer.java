package cn.bdqfork.rpc.protocol.netty;

import cn.bdqfork.rpc.protocol.DataDecoder;
import cn.bdqfork.rpc.protocol.DataEncoder;
import cn.bdqfork.rpc.remote.Serializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author bdq
 * @date 2019-03-05
 */
public class NettyInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelHandlerElement channelHandlerElement;
    private Serializer serializer;

    public NettyInitializer(Serializer serializer, ChannelHandlerElement channelHandlerElement) {
        this.serializer = serializer;
        this.channelHandlerElement = channelHandlerElement;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4))
                .addLast(new DataDecoder(serializer))
                .addLast(new DataEncoder(serializer))
                .addLast(channelHandlerElement.getHandlersToInit());
    }

    public static class ChannelHandlerElement {
        private ChannelHandler[] channelHandlers;

        public ChannelHandlerElement(ChannelHandler... channelHandlers) {
            this.channelHandlers = channelHandlers;
        }

        public ChannelHandler[] getHandlersToInit() {
            return channelHandlers;
        }

    }
}
