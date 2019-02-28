package cn.bdqfork.rpc.invoker;

/**
 * @author bdq
 * @date 2019-02-21
 */
public interface Consumer {

    Object invoke(Invocation invocation, long timeout);

}
