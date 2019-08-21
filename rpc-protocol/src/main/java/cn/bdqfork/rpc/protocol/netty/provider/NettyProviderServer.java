package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.protocol.netty.server.NettyServer;
import cn.bdqfork.rpc.protocol.netty.NettyInitializer;
import cn.bdqfork.rpc.remote.ProviderServer;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.invoker.Invoker;
import cn.bdqfork.rpc.protocol.serializer.HessianSerializer;
import cn.bdqfork.rpc.remote.Serializer;

/**
 * @author bdq
 * @date 2019-03-05
 */
public class NettyProviderServer implements ProviderServer {
    private NettyServer nettyServer;

    public NettyProviderServer(ProtocolConfig protocolConfig, Invoker<RpcResponse> invoker) {
        this(protocolConfig, invoker, new HessianSerializer());
    }

    public NettyProviderServer(ProtocolConfig protocolConfig, Invoker<RpcResponse> invoker, Serializer serializer) {
        this.nettyServer = initNettyServer(protocolConfig, invoker, serializer);
    }

    private NettyServer initNettyServer(ProtocolConfig protocolConfig, Invoker<RpcResponse> invoker, Serializer serializer) {
        InvokerHandler invokerHandler = new InvokerHandler(invoker);

        NettyInitializer.ChannelHandlerElement handlerElement = new NettyInitializer.ChannelHandlerElement(invokerHandler);
        NettyInitializer nettyInitializer = new NettyInitializer(serializer, handlerElement);

        return new NettyServer(protocolConfig.getHost(), protocolConfig.getPort(), nettyInitializer);

    }

    public void start() {
        nettyServer.start();
    }

    public void close() {
        nettyServer.close();
    }

}
