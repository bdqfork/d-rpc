package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.registry.URL;
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

        rpcContext.setArguments(invocation.getArguments());

        String refName = url.getParameter(Const.REF_NAME_KEY);
        rpcContext.setRefName(refName);

        Map<String, String> attachments = new HashMap<>();
        attachments.put(Const.INTERFACE_KEY, invoker.getInterface().getName());

        invocation.setAttachments(attachments);
        rpcContext.setInvocation(invocation);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
