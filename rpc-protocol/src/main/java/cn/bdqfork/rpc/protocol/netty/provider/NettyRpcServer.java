package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.server.NettyServer;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.Serializer;

import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-03-05
 */
public class NettyRpcServer implements RpcServer {
    private NettyServer nettyServer;
    private boolean isRunning;

    public NettyRpcServer(ProtocolConfig protocolConfig, List<Invoker<?>> invokers) {
        this(protocolConfig, invokers, new HessianSerializer());
    }

    public NettyRpcServer(ProtocolConfig protocolConfig, List<Invoker<?>> invokers, Serializer serializer) {
        this.nettyServer = initNettyServer(protocolConfig, invokers, serializer);
    }

    private NettyServer initNettyServer(ProtocolConfig protocolConfig, List<Invoker<?>> invokers, Serializer serializer) {
        InvokerHandler invokerHandler = new InvokerHandler(invokers);

        NettyInitializer.ChannelHandlerElement handlerElement = new NettyInitializer.ChannelHandlerElement(invokerHandler);
        NettyInitializer nettyInitializer = new NettyInitializer(serializer, handlerElement);

        return new NettyServer(protocolConfig.getHost(), protocolConfig.getPort(), nettyInitializer);

    }

    public void start() {
        nettyServer.start();
        isRunning = true;
    }

    public void close() {
        nettyServer.close();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
