package cn.bdqfork.rpc.filter;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.extension.SPI;
import cn.bdqfork.common.Invocation;
import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Result;

/**
 * @author bdq
 * @since 2019-08-23
 */
@SPI
public interface Filter {
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;
}
