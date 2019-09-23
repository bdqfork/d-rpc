package cn.bdqfork.rpc.context.filter;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Activate;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;
import cn.bdqfork.rpc.context.RpcContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-09-04
 */
@Activate(group = Const.PROTOCOL_CONSUMER, order = -1000)
public class RpcContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        RpcContext rpcContext = RpcContext.getRpcContext();

        rpcContext.setUrl(url);
        rpcContext.setInvocation(invocation);
        rpcContext.setMethodName(invocation.getMethodName());
        rpcContext.setParameterTypes(invocation.getParameterTypes());
        rpcContext.setArguments(invocation.getArguments());

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
