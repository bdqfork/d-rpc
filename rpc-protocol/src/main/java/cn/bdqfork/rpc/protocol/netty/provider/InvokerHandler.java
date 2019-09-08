package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.Response;
import cn.bdqfork.rpc.remote.Result;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class InvokerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(InvokerHandler.class);
    private final Map<String, Invoker> invokers = new ConcurrentHashMap<>();

    public InvokerHandler(List<Invoker<?>> invokers) {
        invokers.forEach(invoker -> this.invokers.put(invoker.getInterface().getName(), invoker));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        Invocation invocation = (Invocation) request.getData();

        String serviceInterface = invocation.getAttachments().get(Const.INTERFACE_KEY);
        Invoker<?> invoker = invokers.get(serviceInterface);
        if (invoker != null) {
            Result result = invoker.invoke(invocation);
            Response response = new Response();
            response.setId(request.getId());
            response.setData(result);
            ctx.writeAndFlush(response);
        } else {
            log.warn("there is no service for : {}", serviceInterface);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
