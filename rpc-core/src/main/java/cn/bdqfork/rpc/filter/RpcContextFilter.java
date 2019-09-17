package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.context.RpcContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class RpcContextFilter implements Filter {
    @Override
    public void invoke(Invoker<?> invoker, Invocation invocation) {
        URL url = invoker.getUrl();
        RpcContext rpcContext = RpcContext.getRpcContext();

        rpcContext.setUrl(url);
        rpcContext.setMethodName(invocation.getMethodName());
        rpcContext.setParameterTypes(invocation.getParameterTypes());

        Map<String, Object> attachments = new HashMap<>();
        attachments.put(Const.INTERFACE_KEY, invoker.getInterface().getName());
        attachments.put(Const.VERSION_KEY, url.getParameter(Const.VERSION_KEY));

        invocation.setAttachments(attachments);

        rpcContext.setArguments(invocation.getArguments());
        rpcContext.setInvocation(invocation);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
