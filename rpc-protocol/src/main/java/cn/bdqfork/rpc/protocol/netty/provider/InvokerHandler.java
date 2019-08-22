package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.rpc.remote.context.RpcContext;
import cn.bdqfork.rpc.remote.invoker.Invoker;
import cn.bdqfork.rpc.remote.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class InvokerHandler extends ChannelInboundHandlerAdapter {
    private final Invoker<RpcResponse> invoker;

    public InvokerHandler(Invoker<RpcResponse> invoker) {
        this.invoker = invoker;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = invoker.invoke((RpcContext.Context) msg);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
