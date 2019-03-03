package cn.bdqfork.rpc.protocol.client;

import cn.bdqfork.rpc.consumer.context.RpcContextManager;
import cn.bdqfork.rpc.protocol.NettyChannel;
import cn.bdqfork.rpc.protocol.RpcResponse;
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
        RpcContextManager.doReceived((RpcResponse) msg);
    }

}
