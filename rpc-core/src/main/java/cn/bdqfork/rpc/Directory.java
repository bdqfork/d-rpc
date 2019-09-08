package cn.bdqfork.rpc;

import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019-09-04
 */
public interface Directory<T> extends Node {
    Class<T> getInterface();

    List<Invoker<T>> list(Invocation invocation);
}
