package cn.bdqfork.protocol.rpc.handler;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.rpc.context.RpcContext;
import cn.bdqfork.rpc.protocol.Request;
import cn.bdqfork.rpc.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-02-20
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Invoker> invokers = new ConcurrentHashMap<>();

    public void addInvoker(Invoker<?> invoker) {
        URL url = invoker.getUrl();
        String serviceInterface = url.getParameter(Const.INTERFACE_KEY);
        String version = url.getParameter(Const.VERSION_KEY, "");
        String key = getKey(serviceInterface, version);
        this.invokers.put(key, invoker);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;

        if (request.isHeartbeat()) {
            handleHeartbeat(ctx, request);
            return;
        }

        Invocation invocation = (Invocation) request.getData();

        Map<String, Object> attachments = invocation.getAttachments();

        String serviceInterface = (String) attachments.get(Const.INTERFACE_KEY);
        String version = (String) attachments.getOrDefault(Const.VERSION_KEY, "");

        Invoker<?> invoker = getInvoker(serviceInterface, version);

        if (invoker != null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            RpcContext.getRpcContext()
                    .setRemoteAddress(inetSocketAddress.getHostString(), inetSocketAddress.getPort());

            Response response = new Response();
            response.setId(request.getId());

            Result result = invoker.invoke(invocation);
            response.setData(result);
            ctx.writeAndFlush(response);
        } else {
            RpcException rpcException = new RpcException("There is no service for interface named "
                    + serviceInterface + " and version = " + version);
            Response response = buildErrorResponse(request, rpcException);
            ctx.writeAndFlush(response);
            throw rpcException;
        }
    }

    private Invoker<?> getInvoker(String serviceInterface, String version) {
        if (!StringUtils.isBlank(version)) {
            return invokers.get(getKey(serviceInterface, version));
        }
        for (Map.Entry<String, Invoker> entry : invokers.entrySet()) {
            String key = entry.getKey();
            String interfaceName = key.substring(0, key.indexOf(":"));
            if (interfaceName.equals(serviceInterface)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Response buildErrorResponse(Request request, RpcException rpcException) {
        Response response = new Response();
        response.setId(request.getId());
        response.setStatus(Response.SERVER_ERROR);
        response.setMessage(rpcException.getMessage());
        return response;
    }

    private void handleHeartbeat(ChannelHandlerContext ctx, Request request) {
        if (log.isDebugEnabled()) {
            log.debug("Recevied heartbeart !");
        }
        Response response = new Response();
        response.setId(request.getId());
        response.setHeartbeat(true);
        ctx.channel().writeAndFlush(response);
    }

    private String getKey(String serviceInterface, String version) {
        return serviceInterface + ":" + version;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (log.isDebugEnabled()) {
                log.debug("Idle state close connection !");
            }
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }
}
