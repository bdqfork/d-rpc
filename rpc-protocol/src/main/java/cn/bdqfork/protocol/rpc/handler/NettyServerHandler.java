package cn.bdqfork.protocol.rpc.handler;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;
import cn.bdqfork.rpc.protocol.Request;
import cn.bdqfork.rpc.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Invoker> invokers = new ConcurrentHashMap<>();

    public void addInvoker(Invoker<?> invoker) {
        URL url = invoker.getUrl();
        String serviceInterface = url.getParameter(Const.INTERFACE_KEY);
        String version = url.getParameter(Const.VERSION_KEY, "");
        this.invokers.put(getKey(serviceInterface, version), invoker);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        Invocation invocation = (Invocation) request.getData();

        Map<String, Object> attachments = invocation.getAttachments();

        Invoker<?> invoker = null;

        String serviceInterface = (String) attachments.get(Const.INTERFACE_KEY);
        String version = (String) attachments.getOrDefault(Const.VERSION_KEY, "");

        if (!StringUtils.isBlank(version)) {
            invoker = invokers.get(getKey(serviceInterface, version));
        } else {
            for (Map.Entry<String, Invoker> entry : invokers.entrySet()) {
                String key = entry.getKey();
                String interfaceName = key.substring(0, key.indexOf(":"));
                if (interfaceName.equals(serviceInterface)) {
                    invoker = entry.getValue();
                    break;
                }
            }
        }

        Response response = new Response();
        response.setId(request.getId());
        if (invoker != null) {
            Result result = invoker.invoke(invocation);
            response.setData(result);
            ctx.writeAndFlush(response);
        } else {
            response.setStatus(Response.SERVER_ERROR);
            RpcException rpcException = new RpcException("There is no service for interface named "
                    + serviceInterface + " and version = " + version);
            response.setMessage(rpcException.getMessage());
            ctx.writeAndFlush(response);
            throw rpcException;
        }
    }

    private String getKey(String serviceInterface, String version) {
        return serviceInterface + ":" + version;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }
}
