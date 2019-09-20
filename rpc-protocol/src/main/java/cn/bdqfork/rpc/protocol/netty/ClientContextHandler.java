package cn.bdqfork.rpc.protocol.netty;

import cn.bdqfork.rpc.context.remote.Response;
import cn.bdqfork.rpc.context.DefaultFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class ClientContextHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefaultFuture.received((Response) msg);
    }

}
