package cn.bdqfork.rpc;


/**
 * @author bdq
 * @since 2019-03-05
 */
public interface Exporter<T> {

    /**
     * 注册服务
     */
    void doExport();

    void undoExport();

    Invoker<T> getInvoker();
}
