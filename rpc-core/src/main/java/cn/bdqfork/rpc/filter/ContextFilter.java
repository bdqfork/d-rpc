package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.Activate;
import cn.bdqfork.rpc.context.RpcContext;

/**
 * @author bdq
 * @since 2019/10/9
 */
@Activate(group = Const.PROVIDER, order = -1000)
public class ContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        URL url = invoker.getUrl();
        RpcContext rpcContext = RpcContext.getRpcContext()
                .setUrl(url)
                .setMethodName(invocation.getMethodName())
                .setParameterTypes(invocation.getParameterTypes())
                .setArguments(invocation.getArguments())
                .setLocalAddress(url.getHost(), url.getPort());

        try {
            return invoker.invoke(invocation);
        } finally {
            rpcContext.getAttachments().clear();
        }
    }
}
