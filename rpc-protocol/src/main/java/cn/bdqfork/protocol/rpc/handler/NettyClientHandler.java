package cn.bdqfork.protocol.rpc.handler;

import cn.bdqfork.rpc.context.DefaultFuture;
import cn.bdqfork.rpc.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefaultFuture.received((Response) msg);
    }

}
