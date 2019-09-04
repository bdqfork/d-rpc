package cn.bdqfork.rpc.protocol.netty.client;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.NettyChannel;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
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
    private boolean isRunning;
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
            bootstrap.connect().sync();
            isRunning = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
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
            defaultFuture.cancle();
            log.error(e.getMessage(), e);
        }

        return defaultFuture;
    }

    private void send(Object data) throws RpcException {
        doConnect();
        Channel channel = NettyChannel.getChannel(host, port);
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
        try {
            isRunning = false;
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
