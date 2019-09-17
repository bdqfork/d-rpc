package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Result;

/**
 * @author bdq
 * @since 2019-08-23
 */
public interface Filter {
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

    String getGroup();

    int getOrder();
}
