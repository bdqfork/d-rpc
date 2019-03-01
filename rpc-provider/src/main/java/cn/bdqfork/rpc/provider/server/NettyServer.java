package cn.bdqfork.rpc.provider.server;

import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.DataDecoder;
import cn.bdqfork.rpc.netty.DataEncoder;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.serializer.HessianSerializer;
import cn.bdqfork.rpc.serializer.JdkSerializer;
import cn.bdqfork.rpc.serializer.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private String host;
    private Integer port;
    private Invoker<RpcResponse> invoker;
    private Serializer serializer;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public NettyServer(String host, Integer port, Invoker<RpcResponse> invoker) {
        this(host, port, invoker, new HessianSerializer());
    }

    public NettyServer(String host, Integer port, Invoker<RpcResponse> invoker, Serializer serializer) {
        this.host = host;
        this.port = port;
        this.invoker = invoker;
        this.serializer = serializer;
    }

    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4))
                                    .addLast(new DataDecoder(serializer))
                                    .addLast(new DataEncoder(serializer))
                                    .addLast(new ServerHandler(invoker));
                        }
                    });
            bootstrap.bind(host, port).sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
        }
    }

    public void close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
