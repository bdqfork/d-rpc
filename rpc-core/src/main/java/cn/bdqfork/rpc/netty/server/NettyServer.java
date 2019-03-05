package cn.bdqfork.rpc.netty.server;

import cn.bdqfork.rpc.netty.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
    private NettyInitializer nettyInitializer;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public NettyServer(String host, Integer port, NettyInitializer nettyInitializer) {
        this.host = host;
        this.port = port;
        this.nettyInitializer = nettyInitializer;
    }

    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(nettyInitializer);
            bootstrap.bind(host, port).sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            close();
        }
    }

    public void close() {
        try {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
