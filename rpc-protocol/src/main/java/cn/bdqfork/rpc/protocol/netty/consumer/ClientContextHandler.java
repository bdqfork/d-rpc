package cn.bdqfork.rpc.protocol.netty.consumer;

import cn.bdqfork.rpc.protocol.NettyChannel;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import cn.bdqfork.rpc.remote.context.RpcContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @date 2019-02-20
 */
@ChannelHandler.Sharable
public class ClientContextHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefaultFuture.doReceived((RpcResponse) msg);
    }

}
