package cn.bdqfork.rpc.invoker;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;

/**
 * @author bdq
 * @date 2019-02-28
 */
public interface Invoker<T> {

    T invoke(Invocation invocation) throws RpcException;

}
