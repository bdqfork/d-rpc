package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.RpcResponse;

/**
 * @author bdq
 * @since 2019-02-28
 */
public interface Invoker<T> extends Node<T>{

    RpcResponse invoke(Invocation invocation) throws RpcException;

}
