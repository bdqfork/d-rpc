package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.protocol.netty.server.NettyServer;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.Serializer;

import java.util.Map;

/**
 * @author bdq
 * @since 2019-03-05
 */
public class NettyRpcServer implements RpcServer {
    private NettyServer nettyServer;
    private boolean isRunning;

    public NettyRpcServer(ProtocolConfig protocolConfig, Map<String, Invoker> invoker) {
        this(protocolConfig, invoker, new HessianSerializer());
    }

    public NettyRpcServer(ProtocolConfig protocolConfig, Map<String, Invoker> invoker, Serializer serializer) {
        this.nettyServer = initNettyServer(protocolConfig, invoker, serializer);
    }

    private NettyServer initNettyServer(ProtocolConfig protocolConfig, Map<String, Invoker> invoker, Serializer serializer) {
        InvokerHandler invokerHandler = new InvokerHandler(invoker);

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
