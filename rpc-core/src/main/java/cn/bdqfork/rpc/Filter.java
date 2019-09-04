package cn.bdqfork.rpc;

/**
 * @author bdq
 * @since 2019-08-23
 */
public interface Filter {
    void invoke(Invoker<?> invoker, Invocation invocation);

    int getOrder();
}
