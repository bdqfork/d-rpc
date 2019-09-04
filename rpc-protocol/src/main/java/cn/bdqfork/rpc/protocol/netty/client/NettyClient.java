package cn.bdqfork.rpc.protocol.netty.client;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
    private volatile boolean isRunning = true;
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public NettyClient(String host, Integer port, NettyInitializer nettyInitializer) {
        this.host = host;
        this.port = port;
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(host, port)
                .handler(nettyInitializer);
        doConnect();
    }

    private void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        try {
            ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Channel newChannel = future.channel();
                    if (future.isSuccess()) {
                        channel = newChannel;
                    } else {
                        newChannel.eventLoop().schedule(new Runnable() {
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
        }
    }

    public void reConnect() {
        doConnect();
    }

    @Override
    public DefaultFuture send(Object data, long timeout) {

        Request request = new Request();
        request.setId(Request.newId());
        request.setData(data);

        DefaultFuture defaultFuture = new DefaultFuture(request, timeout);

        try {
            send(request);
        } catch (RpcException e) {
            log.error(e.getMessage(), e);
            defaultFuture.cancle();
        }

        return defaultFuture;
    }

    private void send(Object data) throws RpcException {
        doConnect();
        ChannelFuture future = channel.writeAndFlush(data);
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new RpcException(e);
        }
        if (future.isSuccess()) {
            log.debug("send message success !");
        } else {
            throw new RpcException(future.cause());
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
        return isRunning;
    }

    public void close() {
        isRunning = false;
        log.debug("client closed");
    }
}
