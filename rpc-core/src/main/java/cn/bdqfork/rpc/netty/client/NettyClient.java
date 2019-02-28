package cn.bdqfork.rpc.netty.client;

import cn.bdqfork.rpc.netty.DataDecoder;
import cn.bdqfork.rpc.netty.DataEncoder;
import cn.bdqfork.rpc.netty.NettyChannel;
import cn.bdqfork.rpc.serializer.JdkSerializer;
import cn.bdqfork.rpc.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class NettyClient {
    private Logger log = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private Integer port;
    private Serializer serializer;
    private boolean isRunning;
    private EventLoopGroup group;

    public NettyClient(String host, Integer port) {
        this(host, port, new JdkSerializer());
    }

    public NettyClient(String host, Integer port, Serializer serializer) {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
    }

    public void open() throws InterruptedException {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4))
                                    .addLast(new DataDecoder(serializer))
                                    .addLast(new DataEncoder(serializer))
                                    .addLast(new ClientHandler());
                        }
                    });
            bootstrap.connect().sync();
            isRunning = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
        }
    }

    public void send(Object data) {
        Channel channel = NettyChannel.getChannel(host, port);
        ChannelFuture future = channel.writeAndFlush(data);
        try {
            future.await();
            if (future.isSuccess()) {
                log.debug("send message success !");
            } else {
                log.error("failed send message !", future.cause());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void close() {
        group.shutdownGracefully();
        isRunning = false;
    }
}
