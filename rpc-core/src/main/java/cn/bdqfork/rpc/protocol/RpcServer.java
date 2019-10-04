package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.Invoker;
import cn.bdqfork.common.Node;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RpcServer extends Node {
    void start();

    void addInvoker(Invoker<?> invoker);
}
