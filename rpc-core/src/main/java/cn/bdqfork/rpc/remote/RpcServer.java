package cn.bdqfork.rpc.remote;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RpcServer {
    void start();
    void close();
    boolean isRunning();
}
