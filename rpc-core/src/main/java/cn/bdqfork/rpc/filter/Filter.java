package cn.bdqfork.rpc.filter;

import cn.bdqfork.rpc.remote.Invocation;
import cn.bdqfork.rpc.remote.Invoker;

/**
 * @author bdq
 * @since 2019-08-23
 */
public interface Filter {
    void invoke(Invoker<?> invoker, Invocation invocation);

    int getOrder();
}
