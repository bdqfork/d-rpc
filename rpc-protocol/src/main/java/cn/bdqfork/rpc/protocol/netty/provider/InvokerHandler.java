package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.rpc.RpcInvocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.RpcContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class InvokerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(InvokerHandler.class);
    private final Map<String, Invoker> urlInvokers;

    public InvokerHandler(Map<String, Invoker> urlInvokers) {
        this.urlInvokers = Collections.unmodifiableMap(urlInvokers);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcContext rpcContext = (RpcContext) msg;
        String url = rpcContext.getUrl().buildString();

        Invoker invoker = urlInvokers.get(url);

        Class<?> serviceInterface = invoker.getInterface();
        Method method = serviceInterface.getMethod(rpcContext.getMethodName(), rpcContext.getParameterTypes());
        RpcInvocation rpcInvocation = new RpcInvocation(method, rpcContext.getArguments());
        rpcInvocation.setRpcContext(rpcContext);

        if (invoker != null) {
            RpcResponse response = invoker.invoke(rpcInvocation);
            ctx.writeAndFlush(response);
        } else {
            log.warn("there is no service : {}", url);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
