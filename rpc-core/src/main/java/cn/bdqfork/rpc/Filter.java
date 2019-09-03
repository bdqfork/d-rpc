package cn.bdqfork.rpc;

import cn.bdqfork.rpc.remote.context.RpcContext;

/**
 * @author bdq
 * @since 2019-08-23
 */
public interface Filter {
    void entry(RpcContext context);

    void after(RpcContext context, Object result);

    int getOrder();
}
