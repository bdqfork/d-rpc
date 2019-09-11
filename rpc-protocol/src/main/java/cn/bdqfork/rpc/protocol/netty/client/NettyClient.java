package cn.bdqfork.rpc.protocol.netty.client;

import cn.bdqfork.common.exception.RemoteException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyClient implements RemoteClient {
    private Logger log = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private Integer port;
    private volatile Channel channel;
    private volatile boolean isRunning = true;
    private Bootstrap bootstrap;
    private long timeout = 3000;

    public NettyClient(String host, Integer port, NettyInitializer nettyInitializer) {
        this.host = host;
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(host, port)
                .handler(nettyInitializer);
    }

    private void doConnect() throws RemoteException {
        if (channel != null && channel.isActive()) {
            return;
        }
        synchronized (NettyClient.class) {
            if (channel != null && channel.isActive()) {
                return;
            }
            ChannelFuture channelFuture = bootstrap.connect();
            boolean ret = channelFuture.awaitUninterruptibly(timeout);
            if (ret && channelFuture.isSuccess()) {
                Channel oldChannel = channel;
                channel = channelFuture.channel();
                if (oldChannel != null) {
                    oldChannel.close();
                }
                if (!isRunning) {
                    channel.close();
                }
            } else {
                throw new RemoteException(channelFuture.cause().getMessage());
            }
        }
    }

    @Override
    public DefaultFuture send(Object data) throws RpcException {

        Request request = new Request();
        request.setId(Request.newId());
        request.setData(data);

        DefaultFuture defaultFuture = DefaultFuture.newFuture(request, timeout);

        try {
            doSend(request);
        } catch (RpcException e) {
            defaultFuture.cancel();
            throw e;
        }

        return defaultFuture;
    }

    private void doSend(Object data) throws RpcException {
        doConnect();

        if (!isRunning) {
            throw new RemoteException("Failed to send request " + data + ", cause: The channel " + this + " is closed!");
        }

        ChannelFuture future = channel.writeAndFlush(data);
        boolean ret = future.awaitUninterruptibly(timeout);
        if (ret && future.isSuccess()) {
            log.debug("send message success !");
        } else {
            throw new RpcException(future.cause().getMessage());
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void close() {
        isRunning = false;
        log.debug("client closed");
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "NettyClient{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
