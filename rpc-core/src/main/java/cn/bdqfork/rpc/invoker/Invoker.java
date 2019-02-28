package cn.bdqfork.rpc.invoker;

/**
 * @author bdq
 * @date 2019-02-28
 */
public interface Invoker<T> {

    T invoke(Invocation invocation);

}
