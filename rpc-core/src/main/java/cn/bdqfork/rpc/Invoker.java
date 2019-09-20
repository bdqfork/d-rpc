package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;

/**
 * @author bdq
 * @since 2019-02-28
 */
public interface Invoker<T> extends Node {

    Class<T> getInterface();

    Result invoke(Invocation invocation) throws RpcException;

}
