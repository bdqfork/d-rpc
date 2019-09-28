package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.protocol.rpc.handler.DataDecoder;
import cn.bdqfork.protocol.rpc.handler.DataEncoder;
import cn.bdqfork.protocol.rpc.handler.NettyServerHandler;
import cn.bdqfork.rpc.protocol.AbstractRpcServer;
import cn.bdqfork.rpc.Invoker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyServer extends AbstractRpcServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;
    private NettyServerHandler nettyServerHandler;

    public NettyServer(URL url) {
        super(url);
        this.nettyServerHandler = new NettyServerHandler();
    }

    @Override
    protected void doStart() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 15, 4, 0, 0))
                                    .addLast(new DataDecoder(serializer))
                                    .addLast(new DataEncoder(serializer))
                                    .addLast(new IdleStateHandler(0, 0, 200))
                                    .addLast(nettyServerHandler);
                        }
                    });
            bootstrap.bind(host, port).sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            destroy();
        }
    }

    @Override
    public void addInvoker(Invoker<?> invoker) {
        nettyServerHandler.addInvoker(invoker);
    }

    @Override
    protected void doDestroy() {
        try {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
