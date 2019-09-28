package cn.bdqfork.protocol.rpc.handler;

import cn.bdqfork.rpc.context.DefaultFuture;
import cn.bdqfork.rpc.protocol.Request;
import cn.bdqfork.rpc.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = (Response) msg;
        if (response.isHeartbeat()) {
            if (log.isDebugEnabled()) {
                log.debug("Recevied heartbeart response !");
            }
            return;
        }
        DefaultFuture.received(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Connection lost !");
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (log.isDebugEnabled()) {
                log.debug("Send heartbeart !");
            }
            Request request = new Request();
            request.setId(Request.newId());
            request.setHeartbeat(true);
            ctx.channel().writeAndFlush(request);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
