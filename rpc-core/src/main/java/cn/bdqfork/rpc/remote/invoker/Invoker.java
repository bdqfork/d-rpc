package cn.bdqfork.rpc.remote.invoker;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.context.RpcContext;

/**
 * @author bdq
 * @since 2019-02-28
 */
public interface Invoker<T> {

    T invoke(RpcContext.Context context) throws RpcException;

}
