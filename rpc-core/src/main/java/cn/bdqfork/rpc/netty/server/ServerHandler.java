package cn.bdqfork.rpc.netty.server;

import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.invoker.Provider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Invoker<RpcResponse> invoker;

    public ServerHandler(Invoker<RpcResponse> invoker) {
        this.invoker = invoker;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Invocation request = (Invocation) msg;
        RpcResponse response = invoker.invoke(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
