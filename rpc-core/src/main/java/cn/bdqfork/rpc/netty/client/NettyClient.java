package cn.bdqfork.rpc.netty.client;

import cn.bdqfork.common.exception.ConnectionLostException;
import cn.bdqfork.rpc.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.NettyChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
    private NettyInitializer nettyInitializer;
    private EventLoopGroup group;

    public NettyClient(String host, Integer port, NettyInitializer nettyInitializer) {
        this.host = host;
        this.port = port;
        this.nettyInitializer = nettyInitializer;
    }

    public void open() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(nettyInitializer);
            bootstrap.connect().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
        }
    }

    public void send(Object data) throws ConnectionLostException {
        Channel channel = NettyChannel.getChannel(host, port);
        if (channel == null) {
            throw new ConnectionLostException("connection lost !");
        }
        ChannelFuture future = channel.writeAndFlush(data);
        try {
            future.await();
            if (future.isSuccess()) {
                log.debug("send message success !");
            } else {
                log.error("failed to send message !", future.cause());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public void close() {
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
