package cn.bdqfork.rpc.exporter;


import cn.bdqfork.rpc.remote.Invoker;

/**
 * @author bdq
 * @since 2019-03-05
 */
public interface Exporter<T> {

    /**
     * 注册服务
     */
    void doExport();

    Invoker<T> getInvoker();
}
