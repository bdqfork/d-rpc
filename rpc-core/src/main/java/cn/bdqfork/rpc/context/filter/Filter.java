package cn.bdqfork.rpc.context.filter;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.Result;

/**
 * @author bdq
 * @since 2019-08-23
 */
public interface Filter {
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

    String getGroup();

    int getOrder();
}
