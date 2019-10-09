package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.Activate;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.context.RpcContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-09-04
 */
@Activate(group = Const.CONSUMER_SIDE, order = -1000)
public class ConsumerContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        RpcContext rpcContext = RpcContext.getRpcContext()
                .setUrl(url)
                .setMethodName(invocation.getMethodName())
                .setParameterTypes(invocation.getParameterTypes())
                .setArguments(invocation.getArguments())
                .setLocalAddress(NetUtils.getLocalHost(),0)
                .setRemoteAddress(url.getHost(),url.getPort());

        Map<String, Object> attachments = new HashMap<>();
        attachments.put(Const.INTERFACE_KEY, invoker.getInterface().getName());
        attachments.put(Const.VERSION_KEY, url.getParameter(Const.VERSION_KEY));

        invocation.setAttachments(attachments);

        rpcContext.setAttachments(attachments);

        try {
            return invoker.invoke(invocation);
        } finally {
            rpcContext.getAttachments().clear();
        }
    }

}
