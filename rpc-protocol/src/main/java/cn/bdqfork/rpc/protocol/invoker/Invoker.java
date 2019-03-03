package cn.bdqfork.rpc.protocol.invoker;

import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @date 2019-02-28
 */
public interface Invoker<T> {

    T invoke(Invocation invocation) throws RpcException;

}
