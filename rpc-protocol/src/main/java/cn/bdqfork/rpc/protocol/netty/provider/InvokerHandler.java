package cn.bdqfork.rpc.protocol.netty.provider;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.*;
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

    public void addInvoker(Invoker<?> invoker) {
        URL url = invoker.getUrl();
        String refName = url.getParameter(Const.REF_NAME_KEY, "");
        String serviceInterface = url.getParameter(Const.INTERFACE_KEY);
        this.invokers.put(serviceInterface + refName, invoker);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        Invocation invocation = (Invocation) request.getData();

        Map<String, String> attachments = invocation.getAttachments();
        String serviceInterface = attachments.get(Const.INTERFACE_KEY);
        String refName = attachments.get(Const.REF_NAME_KEY);

        Invoker<?> invoker = invokers.get(serviceInterface + refName);

        Response response = new Response();
        response.setId(request.getId());
        if (invoker != null) {

            Result result = invoker.invoke(invocation);

            if (result.getException() != null) {
                response.setStatus(Response.SERVER_ERROR);
                response.setMessage(result.getMessage());
            }

            response.setData(result);
            ctx.writeAndFlush(response);
        } else {
            response.setStatus(Response.SERVER_ERROR);
            RpcException rpcException = new RpcException(String.format("There is no service for Interface %s and refName %s",
                    serviceInterface, refName));
            response.setMessage(rpcException.getMessage());

            response.setData(new Result(rpcException.getMessage(), rpcException));
            ctx.writeAndFlush(response);
            throw rpcException;
        }
    }

}
