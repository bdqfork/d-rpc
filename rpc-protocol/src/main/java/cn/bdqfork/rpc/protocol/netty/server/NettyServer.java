package cn.bdqfork.rpc.protocol.netty.server;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.protocol.DataDecoder;
import cn.bdqfork.rpc.protocol.DataEncoder;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.provider.InvokerHandler;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyServer implements RpcServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private volatile boolean isRunning;
    private URL url;
    private String host;
    private Integer port;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;
    private InvokerHandler invokerHandler;
    private Serializer serializer;

    public NettyServer(URL url) {
        this.url = url;
        this.host = url.getHost();
        this.port = url.getPort();
        String serializtion = url.getParameter(Const.SERIALIZATION_KEY);
        if ("hessian".equals(serializtion)) {
            this.serializer = new HessianSerializer();
        }
        this.invokerHandler = new InvokerHandler();
    }

    @Override
    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 14, 4, 0, 0))
                                    .addLast(new DataDecoder(serializer))
                                    .addLast(new DataEncoder(serializer))
                                    .addLast(invokerHandler);
                        }
                    });
            bootstrap.bind(host, port).sync();
            isRunning = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            destroy();
        }
    }

    @Override
    public void addInvoker(Invoker<?> invoker) {
        invokerHandler.addInvoker(invoker);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return isRunning;
    }

    @Override
    public void destroy() {
        isRunning = false;
        try {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
