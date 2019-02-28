package cn.bdqfork.rpc.netty.client;

import cn.bdqfork.rpc.netty.DefaultFuture;
import cn.bdqfork.rpc.netty.NettyChannel;
import cn.bdqfork.rpc.netty.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefaultFuture.doReceived((RpcResponse) msg);
    }

}
