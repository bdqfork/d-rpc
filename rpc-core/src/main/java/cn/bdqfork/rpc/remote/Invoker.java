package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Node;
import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Result;

/**
 * @author bdq
 * @since 2019-02-28
 */
public interface Invoker<T> extends Node {

    Class<T> getInterface();

    Result invoke(Invocation invocation) throws RpcException;

}
