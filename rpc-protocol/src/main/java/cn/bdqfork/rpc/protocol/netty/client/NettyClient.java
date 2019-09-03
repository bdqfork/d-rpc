package cn.bdqfork.rpc.protocol.netty.client;

import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyClient implements RemoteClient {
    private Logger log = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private Integer port;
    private Channel channel;
    private NettyInitializer nettyInitializer;
    private EventLoopGroup group;

    public NettyClient(String host, Integer port, NettyInitializer nettyInitializer) {
        this.host = host;
        this.port = port;
        this.nettyInitializer = nettyInitializer;
        doConnect();
    }

    private void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(nettyInitializer);
            ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        log.info("Connect to server successful !");
                    } else {
                        log.info("Failed to connect server !");
                        future.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                            }
                        }, 10, TimeUnit.SECONDS);
                    }
                }
            }).sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
        }
    }

    public void reConnect() {
        doConnect();
    }

    public void send(Object data) {
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

    @Override
    public boolean isRunning() {
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }

    public void close() {
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
